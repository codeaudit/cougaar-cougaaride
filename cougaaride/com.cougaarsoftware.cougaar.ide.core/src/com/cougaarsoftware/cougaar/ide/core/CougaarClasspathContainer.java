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
 * Holds all required Cougaar jars for a particular version
 *
 * @author soster
 */
public class CougaarClasspathContainer implements IClasspathContainer {
    private String installPrefix;
    private List jarList;

    /**
     * Creates a new CougaarClasspathContainer object.
     *
     * @param installPrefix install location (e.g. C:\COUGAAR10)
     */
    public CougaarClasspathContainer(String installPrefix) {
        this.installPrefix = installPrefix;
    }

    /**
     * Returns description
     *
     * @return the description
     */
    public String toString() {
        return getDescription();
    }


    /**
     * Returns classpath entries for each jar found in lib and sys
     *
     * @return classpath entries for each jar found in lib and sys
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

        //check for a valid prefix
        File installHome = new File(installPrefix);
        if (installHome.exists() && installHome.isDirectory()
            && installHome.canRead()) {
        } else {
            CougaarPlugin.logError(
                "CougaarClasspathContainer cougaar install home ("
                + installPrefix + " ) was not readable directory.");
            return;

        }

        //look in the sys dir
        File sys = null;
        try {
            sys = new File(installHome.getCanonicalPath() + File.separatorChar
                    + "sys");

            //get a list of jars and zips and add them to the container
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

        //get the lib dir
        File lib = null;
        try {
            lib = new File(installHome.getCanonicalPath() + File.separatorChar
                    + "lib");

            //get a list of jars and add them to the container
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


    /**
     * Add every jar in the directory to the library entry
     *
     * @param dir parent directory
     * @param filenamearr the jar names
     */
    private void addDirectoryListAsJars(File dir, String[] filenamearr) {
        for (int i = 0; i < filenamearr.length; i++) {
            String string = filenamearr[i];
            jarList.add(JavaCore.newLibraryEntry(
                    new Path(dir.getPath() + File.separatorChar + string),
                    new Path(installPrefix), null, false));
        }
    }


    /**
     * Returns the classpath name
     *
     * @return Cougaar Required Libraries
     */
    public String getDescription() {
        return "Cougaar Required Libraries";
    }


    /**
     * Returns K_APPLICATION
     *
     * @return K_APPLICATION
     */
    public int getKind() {
        return K_APPLICATION;
    }


    /**
     * Gets the Container path
     *
     * @return the Container path
     */
    public IPath getPath() {
        return new Path(IResourceIDs.CLASSPATH_CONTAINER_ID);

    }
}
