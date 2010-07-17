//=========================================================================
//
//  Part of units package -- a Java version of GNU Units program.
//
//  Units is a program for unit conversion originally written in C
//  by Adrian Mariano (adrian@cam.cornell.edu.).
//  Copyright (C) 1996, 1997, 1999, 2000, 2001, 2002, 2003, 2004,
//  2005, 2006, 2007 by Free Software Foundation, Inc.
//
//  Java version Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008,
//  2009 by Roman R Redziejowski (roman.redz@tele2.se).
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program. If not, see <http://www.gnu.org/licenses/>.
//
//-------------------------------------------------------------------------
//
//  Change log
//
//    050315 Version 1.84.J07. Changed package name to "units".
//    091024 Version 1.87.J01.
//           Redefined 'insertAlph' using generics.
//    091031 Replaced 'addtolist' by 'isCompatibleWith'.
//           Implemented Comparable interface.
//    091101 Removed 'insertAlph'.
//
//=========================================================================

package net.sourceforge.unitsinjava;




//HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
//
//  class Entity
//
//HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
/**
 *  A unit, prefix, or function.
 */

public abstract class Entity implements Comparable<Entity>
{
  /** Name of this Entity. */
  public String name;

  /** Where this Entity is defined. */
  Location location;

  //=====================================================================
  /** Constructor.
   *
   *  @param nam name.
   *  @param loc where defined. */
  //=====================================================================
  Entity(final String nam, Location loc)
    {
      name = nam;
      location = loc;
    }


  //=====================================================================
  //  Comparator.
  //=====================================================================
  public int compareTo(Entity e)
    { return name.compareTo(e.name); }


  //=====================================================================
  //  Check the definition.Used in 'checkunits'.
  //=====================================================================
  public abstract void check();


  //=====================================================================
  //  Return true this Entity is compatible with Value 'v',
  //=====================================================================
  public abstract boolean isCompatibleWith(final Value v);


  //=====================================================================
  //  Return short description of the defined object
  //  to be shown by 'tryallunits'.
  //=====================================================================
  public abstract String desc();

}

