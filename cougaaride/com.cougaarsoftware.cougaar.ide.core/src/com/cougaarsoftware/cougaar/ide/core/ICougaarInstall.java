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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    String getId();


    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     */
    void setId(String id);


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    File getInstallLocation();


    /**
     * DOCUMENT ME!
     *
     * @param installLocation DOCUMENT ME!
     */
    void setInstallLocation(File installLocation);
}
