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
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;


/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.1 $
 * @author $author$
 */
public class CougaarClasspathContainer implements IClasspathContainer {
    private String installPrefix;
    private List jarList;

    /**
     * Creates a new CougaarClasspathContainer object.
     *
     * @param installPrefix DOCUMENT ME!
     */
    public CougaarClasspathContainer(String installPrefix) {
        this.installPrefix = installPrefix;
    }

    /**
     * Returns description
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return getDescription();
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public IClasspathEntry[] getClasspathEntries() {
        if (jarList == null) {
            buildJarList();
        }

        return (IClasspathEntry[]) jarList.toArray(new IClasspathEntry[jarList
            .size()]);
    }


    /**
     * Create the internal storage of the jar files
     */
    private void buildJarList() {
        this.jarList = new ArrayList(65);
        if ((installPrefix == null) || installPrefix.trim().equals("")) {
            //can't do anything cause we dont have an install path yet
            return;
        }

        File installHome = new File(installPrefix);
        if (installHome.exists() && installHome.isDirectory()
            && installHome.canRead()) {
        } else {
            CougaarPlugin.logError(
                "CougaarClasspathContainer cougaar install home ("
                + installPrefix + " ) was not readable directory.");
            return;

        }

        File sys = null;
        try {
            sys = new File(installHome.getCanonicalPath() + File.separatorChar
                    + "sys");

            if (sys.exists() && sys.isDirectory() && sys.canRead()) {
                String[] filenamearr = sys.list(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".jar")
                                || name.endsWith(".zip");
                            }
                        });

                addDirectoryListAsJars(sys, filenamearr);
            } else {
                CougaarPlugin.logError(
                    "CougaarClasspathContainer cougaar install  sys ("
                    + installPrefix + " ) was not readable directory.");
                return;

            }
        } catch (IOException e) {
            CougaarPlugin.log(e);
        }

        File lib = null;
        try {
            lib = new File(installHome.getCanonicalPath() + File.separatorChar
                    + "lib");

            if (lib.exists() && lib.isDirectory() && lib.canRead()) {
                String[] filenamearr = lib.list(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".jar");
                            }
                        });

                addDirectoryListAsJars(lib, filenamearr);
            } else {
                CougaarPlugin.logError(
                    "CougaarClasspathContainer cougaar install lib ("
                    + installPrefix + " ) was not readable directory.");
                return;

            }
        } catch (IOException e) {
            CougaarPlugin.log(e);
        }
    }


    private void addDirectoryListAsJars(File dir, String[] filenamearr) {
        for (int i = 0; i < filenamearr.length; i++) {
            String string = filenamearr[i];
            jarList.add(JavaCore.newLibraryEntry(
                    new Path(dir.getPath() + File.separatorChar + string),
                    null, null, false));
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return "Cougaar Required Libraries";
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getKind() {
        return K_APPLICATION;
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public IPath getPath() {
        return new Path(IResourceIDs.CLASSPATH_CONTAINER_ID);

    }
}
