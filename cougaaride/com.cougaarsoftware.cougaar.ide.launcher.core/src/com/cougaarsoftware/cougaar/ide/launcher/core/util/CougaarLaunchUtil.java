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


package com.cougaarsoftware.cougaar.ide.launcher.core.util;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.cougaarsoftware.cougaar.ide.launcher.core.LauncherPlugin;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public class CougaarLaunchUtil {
    /**
     * Gets the arrayFromList attribute of the ServerLaunchUtil object
     *
     * @param configuration Description of the Parameter
     * @param attribute Description of the Parameter
     *
     * @return The arrayFromList value
     */
    public static String[] getArrayFromList(
        ILaunchConfiguration configuration, String attribute) {
        List list = null;

        try {
            if ((list = configuration.getAttribute(attribute, (List) null)) == null) {
                return null;
            }
        } catch (CoreException e) {
            LauncherPlugin.log(e);
        }

        return (String[]) list.toArray(new String[list.size()]);
    }


    /**
     * DOCUMENT ME!
     *
     * @param text
     * @param separator
     * @param list
     *
     * @return
     */
    public static List appendListElementToString(String text, String separator,
        List list) {
        ArrayList newList = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            newList.add(text + separator + list.get(i));
        }

        return newList;
    }


    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     * @param parentDir DOCUMENT ME!
     * @param attribute DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CoreException DOCUMENT ME!
     */
    public static boolean isValidDirectory(ILaunchConfiguration configuration,
        String parentDir, String attribute) throws CoreException {
        List list = configuration.getAttribute(attribute, Collections.EMPTY_LIST);

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            File file = new File(parentDir + File.separator
                    + (String) iter.next());
            if (!file.isFile()) {
                return false;
            }
        }

        return true;
    }
}
