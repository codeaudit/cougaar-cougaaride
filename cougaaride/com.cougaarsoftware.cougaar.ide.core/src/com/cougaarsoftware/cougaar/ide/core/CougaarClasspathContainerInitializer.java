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


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;


/**
 *
 */
public class CougaarClasspathContainerInitializer
    extends ClasspathContainerInitializer {
    /**
     * Creates a new CougaarClasspathContainerInitializer object.
     */
    public CougaarClasspathContainerInitializer() {
        super();
    }

    /**
     * Signal this is able to update itself.
     *
     * @param containerPath DOCUMENT ME!
     * @param project DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean canUpdateClasspathContainer(IPath containerPath,
        IJavaProject project) {
        return true;
    }


    /**
     * Re-read the project properties and rebuild the cp based on the cougaar
     * install path
     *
     * @param containerPath DOCUMENT ME!
     * @param project DOCUMENT ME!
     * @param containerSuggestion DOCUMENT ME!
     *
     * @throws CoreException DOCUMENT ME!
     */
    public void requestClasspathContainerUpdate(IPath containerPath,
        IJavaProject project, IClasspathContainer containerSuggestion)
        throws CoreException {
        populateCougaarContainer(project);
    }


    /**
     * DOCUMENT ME!
     *
     * @param containerPath DOCUMENT ME!
     * @param javaProject DOCUMENT ME!
     *
     * @throws CoreException DOCUMENT ME!
     */
    public void initialize(IPath containerPath, IJavaProject javaProject)
        throws CoreException {
        populateCougaarContainer(javaProject);
    }


    private void populateCougaarContainer(IJavaProject javaProject)
        throws CoreException {
        IProject project = javaProject.getProject();
        if (CougaarPlugin.isCougaarProject(project)) {
            //TODO: need to get version from project
            String version = "10.2";
            String installPrefix = CougaarPlugin.getCougaarBaseLocation(version);

            IPath path = new Path(IResourceIDs.CLASSPATH_CONTAINER_ID);
            CougaarClasspathContainer container = new CougaarClasspathContainer(installPrefix);

            IJavaProject[] javaProjects = new IJavaProject[] {
                    JavaCore.create(project)
                };
            IClasspathContainer[] containers = new IClasspathContainer[] {
                    container
                };
            JavaCore.setClasspathContainer(path, javaProjects, containers, null);
        }
    }
}
