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


package com.cougaarsoftware.cougaar.ide.launcher.ui.configuration;


import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public class CougaarParameters {
    private static final String BUNDLE_NAME = "com.cougaarsoftware.cougaar.ide.launcher.ui.configuration.CougaarParameters";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
        .getBundle(BUNDLE_NAME);

    /**
     * Constructor for the LauncherMessages object
     */
    private CougaarParameters() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Enumeration getKeys() {
        return RESOURCE_BUNDLE.getKeys();
    }


    /**
     * Gets the string attribute of the LauncherMessages class
     *
     * @param key Description of the Parameter
     *
     * @return The string value
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);

        } catch (MissingResourceException e) {
            return '!' + key + '!';

        }
    }
}
