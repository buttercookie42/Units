package info.staticfree.android.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class UnitsContentProvider extends ContentProvider {
	public final static String AUTHORITY = "info.staticfree.android.units";
	public final static String
    	TYPE_HISTORY_ENTRY_ITEM = "vnd.android.cursor.item/vnd.info.staticfree.android.units.history_entry",
    	TYPE_HISTORY_ENTRY_DIR  = "vnd.android.cursor.dir/vnd.info.staticfree.android.units.history_entry",
    	TYPE_UNIT_USAGE_ITEM    = "vnd.android.cursor.item/vnd.info.staticfree.android.units.unit_usage",
    	TYPE_UNIT_USAGE_DIR     = "vnd.android.cursor.dir/vnd.info.staticfree.android.units.unit_usage",
    	TYPE_CLASSIFICATION_ITEM = "vnd.android.cursor.item/vnd.info.staticfree.android.units.classification",
    	TYPE_CLASSIFICATION_DIR  = "vnd.android.cursor.dir/vnd.info.staticfree.android.units.classification";

	private final static int
		MATCHER_HISTORY_ENTRY_ITEM = 1,
		MATCHER_HISTORY_ENTRY_DIR  = 2,
		MATCHER_UNIT_USAGE_ITEM    = 3,
		MATCHER_UNIT_USAGE_DIR     = 4,
		MATCHER_UNIT_USAGE_CONFORM_TOP_DIR = 5,
		MATCHER_CLASSIFICATION_ITEM = 6,
		MATCHER_CLASSIFICATION_DIR  = 7,
		MATCHER_CLASSIFICATION_ITEM_FPRINT = 8;

    private static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, HistoryEntry.PATH, MATCHER_HISTORY_ENTRY_DIR);
        uriMatcher.addURI(AUTHORITY, HistoryEntry.PATH+"/#", MATCHER_HISTORY_ENTRY_ITEM);

        uriMatcher.addURI(AUTHORITY, UsageEntry.PATH, MATCHER_UNIT_USAGE_DIR);
        uriMatcher.addURI(AUTHORITY, UsageEntry.PATH + "/#", MATCHER_UNIT_USAGE_ITEM);

        uriMatcher.addURI(AUTHORITY, UsageEntry.PATH_CONFORM_TOP, MATCHER_UNIT_USAGE_CONFORM_TOP_DIR);

        uriMatcher.addURI(AUTHORITY, ClassificationEntry.PATH, MATCHER_CLASSIFICATION_DIR);
        uriMatcher.addURI(AUTHORITY, ClassificationEntry.PATH + "/#", MATCHER_CLASSIFICATION_ITEM);
        uriMatcher.addURI(AUTHORITY, ClassificationEntry.PATH + "/" + ClassificationEntry.PATH_BY_FPRINT + "/*", MATCHER_CLASSIFICATION_ITEM_FPRINT);
    }

	public final static String
		HISTORY_ENTRY_TABLE_NAME = "history";

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private final static String DB_NAME = "content.db";
		private final static int DB_VER = 1;


        public DatabaseHelper(Context context) {
        	super(context, DB_NAME, null, DB_VER);
        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "+ HISTORY_ENTRY_TABLE_NAME + " (" +
				HistoryEntry._ID + " INTEGER PRIMARY KEY," +
				HistoryEntry._HAVE + " TEXT," +
				HistoryEntry._WANT + " TEXT," +
				HistoryEntry._RESULT + " REAL," +
				HistoryEntry._WHEN + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
			");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + HISTORY_ENTRY_TABLE_NAME);

		}

	}

    private DatabaseHelper dbHelper;
    private UnitUsageDBHelper unitDbHelper;

	@Override
	public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        unitDbHelper = new UnitUsageDBHelper(getContext());
        return true;
	}



	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)){
		case MATCHER_HISTORY_ENTRY_DIR:
			return TYPE_HISTORY_ENTRY_DIR;
		case MATCHER_HISTORY_ENTRY_ITEM:
			return TYPE_HISTORY_ENTRY_ITEM;

		case MATCHER_UNIT_USAGE_DIR:
			return TYPE_UNIT_USAGE_DIR;
		case MATCHER_UNIT_USAGE_ITEM:
			return TYPE_UNIT_USAGE_ITEM;
		case MATCHER_UNIT_USAGE_CONFORM_TOP_DIR:
			return TYPE_UNIT_USAGE_DIR;

		case MATCHER_CLASSIFICATION_DIR:
			return TYPE_CLASSIFICATION_DIR;
		case MATCHER_CLASSIFICATION_ITEM:
		case MATCHER_CLASSIFICATION_ITEM_FPRINT:
			return TYPE_CLASSIFICATION_ITEM;

        default:
                throw new IllegalArgumentException("Cannot get type for URI "+uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues cv) {

		Uri newItem;
		switch (uriMatcher.match(uri)){
		case MATCHER_HISTORY_ENTRY_DIR:{
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			newItem = ContentUris.withAppendedId(HistoryEntry.CONTENT_URI, db.insert(HISTORY_ENTRY_TABLE_NAME, null, cv));
		}break;

		case MATCHER_UNIT_USAGE_DIR:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();
			newItem = ContentUris.withAppendedId(UsageEntry.CONTENT_URI, db.insert(UnitUsageDBHelper.DB_USAGE_TABLE, null, cv));
		}break;

        default:
                throw new IllegalArgumentException("Cannot insert into "+uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return newItem;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

        long id;
        Cursor c;
		switch (uriMatcher.match(uri)){
		case MATCHER_HISTORY_ENTRY_DIR:{
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
	        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

			qb.setTables(HISTORY_ENTRY_TABLE_NAME);
			c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		}break;

		case MATCHER_HISTORY_ENTRY_ITEM:{
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
	        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

			qb.setTables(HISTORY_ENTRY_TABLE_NAME);
            id = ContentUris.parseId(uri);
            qb.appendWhere(HistoryEntry._ID + "="+id);
			c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		}break;

		case MATCHER_UNIT_USAGE_DIR:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();
	        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        qb.setTables(UnitUsageDBHelper.DB_USAGE_TABLE);

	        c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		}break;

		case MATCHER_UNIT_USAGE_ITEM:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();
	        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        qb.setTables(UnitUsageDBHelper.DB_USAGE_TABLE);
            id = ContentUris.parseId(uri);
            qb.appendWhere(UsageEntry._ID + "="+id);
	        c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		}break;

		case MATCHER_UNIT_USAGE_CONFORM_TOP_DIR:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();

			// Below is a sub-query needed in order to have the GROUP BY -reduced items
			// be the most used unit with the given fingerprint. The last item in the list is the one that's used.
			final String tables =
				"(SELECT "+
				UnitUsageDBHelper.DB_USAGE_TABLE +"."+UsageEntry._ID+" AS "+UsageEntry._ID+","+
				UnitUsageDBHelper.DB_USAGE_TABLE +"."+UsageEntry._FACTOR_FPRINT+" AS "+UsageEntry._FACTOR_FPRINT+","+
				UsageEntry._USE_COUNT+","+
				"IFNULL("+ClassificationEntry._DESCRIPTION +
				"|| ' (' || " + UsageEntry._UNIT + "|| ')'" +
				"," + UsageEntry._UNIT + ") AS " +UsageEntry._UNIT+
					" FROM "+UnitUsageDBHelper.DB_USAGE_TABLE +
				" LEFT JOIN " + UnitUsageDBHelper.DB_CLASSIFICATION_TABLE + " ON " +
					UnitUsageDBHelper.DB_USAGE_TABLE + "." + UsageEntry._FACTOR_FPRINT+
					"=" +
					UnitUsageDBHelper.DB_CLASSIFICATION_TABLE + "." + ClassificationEntry._FACTOR_FPRINT+
				" ORDER BY "+UsageEntry._USE_COUNT+" ASC, "+UnitUsageDBHelper.DB_USAGE_TABLE +"."+UsageEntry._UNIT+" DESC)";
			c = db.query(
					tables,
					projection, selection, selectionArgs, UsageEntry._FACTOR_FPRINT, UsageEntry._FACTOR_FPRINT+" NOTNULL", sortOrder);
			// an extra watcher to keep list in sync with the units
			//c.setNotificationUri(getContext().getContentResolver(), UsageEntry.CONTENT_URI);
		}break;

		case MATCHER_CLASSIFICATION_DIR:{
			final SQLiteDatabase db = unitDbHelper.getReadableDatabase();
			c = db.query(UnitUsageDBHelper.DB_CLASSIFICATION_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		}break;

		case MATCHER_CLASSIFICATION_ITEM:{
			final SQLiteDatabase db = unitDbHelper.getReadableDatabase();
	        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

			qb.setTables(UnitUsageDBHelper.DB_CLASSIFICATION_TABLE);
            id = ContentUris.parseId(uri);
            qb.appendWhere(ClassificationEntry._ID + "="+id);
			c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		}

		case MATCHER_CLASSIFICATION_ITEM_FPRINT:{
			final SQLiteDatabase db = unitDbHelper.getReadableDatabase();
	        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

			qb.setTables(UnitUsageDBHelper.DB_CLASSIFICATION_TABLE);
            final String fprint = uri.getLastPathSegment();
            qb.appendWhere(ClassificationEntry._FACTOR_FPRINT + "=?");

            List<String> selectionArgs2;
            if (selectionArgs == null){
            	selectionArgs2 = new ArrayList<String>();
            }else{
            	selectionArgs2 = Arrays.asList(selectionArgs);
            }
            selectionArgs2.add(0, fprint);
			c = qb.query(db, projection, selection, selectionArgs2.toArray(new String[]{}), null, null, sortOrder);
		}break;

			default:
				throw new IllegalArgumentException("Cannot query "+uri);
		}

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int numUpdated;
		switch (uriMatcher.match(uri)){
		case MATCHER_HISTORY_ENTRY_DIR:{
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			numUpdated = db.update(HISTORY_ENTRY_TABLE_NAME, values, selection, selectionArgs);
		}break;

		case MATCHER_HISTORY_ENTRY_ITEM:{
			final SQLiteDatabase db = dbHelper.getWritableDatabase();

			final long id = ContentUris.parseId(uri);
			if (selection != null){
				selection = "("+selection+")"+ " AND "+HistoryEntry._ID+"="+id+"";
			}else{
				selection = HistoryEntry._ID+"="+id;
			}
			numUpdated = db.update(HISTORY_ENTRY_TABLE_NAME, values, selection, selectionArgs);
		}break;

		case MATCHER_UNIT_USAGE_DIR:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();
			numUpdated = db.update(UnitUsageDBHelper.DB_USAGE_TABLE, values, selection, selectionArgs);
		}break;

		case MATCHER_UNIT_USAGE_ITEM:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();

			final long id = ContentUris.parseId(uri);
			if (selection != null){
				selection = "("+selection+")"+ " AND "+UsageEntry._ID+"="+id+"";
			}else{
				selection = UsageEntry._ID+"="+id;
			}
			numUpdated = db.update(UnitUsageDBHelper.DB_USAGE_TABLE, values, selection, selectionArgs);
		}break;

		default:
			throw new IllegalArgumentException("Cannot update "+uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return numUpdated;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {

		int numDeleted;
		switch (uriMatcher.match(uri)){
		case MATCHER_HISTORY_ENTRY_DIR:{
			final SQLiteDatabase db = dbHelper.getWritableDatabase();

			numDeleted = db.delete(HISTORY_ENTRY_TABLE_NAME, where, whereArgs);
		}break;

		case MATCHER_HISTORY_ENTRY_ITEM:{
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			final long id = ContentUris.parseId(uri);
			if (where != null){
				where = "("+where+")"+ " AND "+HistoryEntry._ID+"="+id+"";
			}else{
				where = HistoryEntry._ID+"="+id;
			}
			numDeleted = db.delete(HISTORY_ENTRY_TABLE_NAME, where, whereArgs);
		}break;

		case MATCHER_UNIT_USAGE_DIR:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();

			numDeleted = db.delete(UnitUsageDBHelper.DB_USAGE_TABLE, where, whereArgs);
		}break;

		case MATCHER_UNIT_USAGE_ITEM:{
			final SQLiteDatabase db = unitDbHelper.getWritableDatabase();
			final long id = ContentUris.parseId(uri);
			if (where != null){
				where = "("+where+")"+ " AND "+UsageEntry._ID+"="+id+"";
			}else{
				where = UsageEntry._ID+"="+id;
			}
			numDeleted = db.delete(UnitUsageDBHelper.DB_USAGE_TABLE, where, whereArgs);
		}break;

		default:
			throw new IllegalArgumentException("Cannot delete "+uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return numDeleted;
	}

}
