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


package com.cougaarsoftware.cougaar.ide.launcher.ui.util;


import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.cougaarsoftware.cougaar.ide.launcher.core.LauncherPlugin;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public class CougaarLaunchUIUtil {
    /**
     * Gets the name attribute of the ServerLaunchUIUtil class
     *
     * @param configuration Description of the Parameter
     *
     * @return The name value
     */
    public static String getName(ILaunchConfiguration configuration) {
        try {
            return configuration.getType().getName() + ": "
            + configuration.getName();

        } catch (CoreException e) {
            LauncherPlugin.log(e);

            return null;

        }
    }


    /**
     * Helper that opens the directory chooser dialog.
     *
     * @param startingDirectory Description of the Parameter
     * @param shell Description of the Parameter
     *
     * @return The directory value
     */
    public static File getDirectory(File startingDirectory, Shell shell) {
        DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN);

        if (startingDirectory != null) {
            fileDialog.setFilterPath(startingDirectory.getPath());
        }

        String dir = fileDialog.open();

        if (dir != null) {
            dir = dir.trim();
            if (dir.length() > 0) {
                return new File(dir);
            }
        }

        return null;
    }
}
