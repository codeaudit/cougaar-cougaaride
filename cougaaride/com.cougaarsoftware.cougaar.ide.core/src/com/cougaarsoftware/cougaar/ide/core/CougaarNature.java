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
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;


/**
 * Simple implementation of a nature.
 */
public class CougaarNature implements IProjectNature {
    // To hold associated project reference
    private IProject project;

    // switch to control write of trace data
    private boolean traceEnabled = false;

    /**
     * CougaarNature default constructor.
     */
    public CougaarNature() {
        super();
    }

    /**
     * Customizes the project by adding a builder, the ReadmeBuilder in this
     * scenario.
     *
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() throws CoreException {
    }


    /**
     * Write trace statements.   System.out.println with prefix tagging used
     * for simplicity.
     *
     * @param msg DOCUMENT ME!
     */
    void traceMsg(String msg) {
        if (traceEnabled) {
            System.out.println(msg);
        }
    }


    /**
     * Removes any nature customization and private resources as may be
     * required.
     * 
     * <p>
     * The ReadmeBuilder is not removed here, but is removed automatically  by
     * the platform as the plugin.xml definition links the builder to the
     * nature.
     * </p>
     * 
     * <p>
     * This works when the nature identifies the builder using a  builder
     * id="a.b.c" entry and the  builder states that it has a nature using a
     * builder hasNature="true" entry.
     * </p>
     *
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() throws CoreException {
        System.err.println("About to deconfigure a Cougaar project!!!");
    }


    /**
     * Returns local reference to associated project
     *
     * @see org.eclipse.core.resources.IProjectNature#getProject()
     */
    public IProject getProject() {
        return project;
    }


    /**
     * Saves local reference to associated project.
     *
     * @see org.eclipse.core.resources.IProjectNature#setProject(IProject)
     */
    public void setProject(IProject value) {
        project = value;
    }
}
