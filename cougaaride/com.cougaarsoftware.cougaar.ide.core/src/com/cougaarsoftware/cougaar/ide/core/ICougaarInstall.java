/*
 * Cougaar IDE
 *
 * Copyright (C) 2003, Cougaar Software, Inc. <tcarrico@cougaarsoftware.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */


package com.cougaarsoftware.cougaar.ide.core;


import java.io.File;


/**
 * The interface for representing Cougaar Installations
 *
 * @author mabrams
 */
public interface ICougaarInstall {
    /**
     * gets the unique id for the cougaar install instance
     *
     * @return the unique id for the cougaar install instance
     */
    String getId();


    /**
     * sets the unique id for the cougaar install instance
     *
     * @param id the unique id
     */
    void setId(String id);


    /**
     * gets the cougaar installation
     *
     * @return the cougaar install location
     */
    File getInstallLocation();


    /**
     * Sets the cougaar install location
     *
     * @param installLocation the cougaar install location
     */
    void setInstallLocation(File installLocation);


    /**
     * Compares two ICougaarInstall objects
     *
     * @param obj the comparison object
     *
     * @return true if the two objects are equal
     */
    public boolean equals(Object obj);


    /**
     * returns the hash code for this object
     *
     * @return the integer hash code
     */
    public int hashCode();
}
