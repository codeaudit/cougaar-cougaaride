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
 * An abstract implementation of <code>ICougaarInstall</code> providing utility
 * methods for subclasses implementing the <code>ICougaarInstall</code> 
 * interface
 *
 * @author mabrams
 */
public abstract class AbstractCougaarInstall implements ICougaarInstall {
    private String fId = "";
    private File fInstallLocation;

    /**
     * Creates a new AbstractCougaarInstall object.
     *
     * @param id DOCUMENT ME!
     * @param location DOCUMENT ME!
     */
    public AbstractCougaarInstall(String id, String location) {
        if (id == null) {
            throw new IllegalArgumentException(CoreMessages.getString(
                    "cougaarInstall.assert.idNotNull"));
        }

        fId = id;
        fInstallLocation = new File(location);
    }

    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.launcher.core.ICougaarInstall#getId()
     */
    public String getId() {
        return fId;
    }


    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.launcher.core.ICougaarInstall#setId(java.lang.String)
     */
    public void setId(String id) {
        this.fId = id;

    }


    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.launcher.core.ICougaarInstall#getInstallLocation()
     */
    public File getInstallLocation() {
        return fInstallLocation;
    }


    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.launcher.core.ICougaarInstall#setInstallLocation(java.io.File)
     */
    public void setInstallLocation(File installLocation) {
        // TODO notify interested parties if location changes
        fInstallLocation = installLocation;


    }
}
