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


import java.io.IOException;


/**
 * Represents a Cougaar installation
 *
 * @author mabrams
 */
public class CougaarInstall extends AbstractCougaarInstall {
    /**
     * constructor for CougaarInstall objects
     *
     * @param id the id of the cougaar install
     * @param location the location of the install
     */
    public CougaarInstall(String id, String location) {
        super(id, location);
    }

    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.core.ICougaarInstall#hashCode(java.lang.Object)
     */
    public int hashCode() {
        return this.getId().hashCode();
    }


    /**
     * Checks to see if two CougaarInstall instances are equal.  Comaprison is
     * based on the coanonical path for the install location of each object.
     *
     * @param obj the object to compare two
     *
     * @return true if the two objects are equal
     */
    public boolean equals(Object obj) {
        if (obj instanceof CougaarInstall) {
            CougaarInstall c2 = (CougaarInstall) obj;

            try {
                if ((this.getId().equals(c2.getId()))
                    && (this.getInstallLocation().getCanonicalPath().equals(c2.getInstallLocation()
                                                                              .getCanonicalPath()))) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
