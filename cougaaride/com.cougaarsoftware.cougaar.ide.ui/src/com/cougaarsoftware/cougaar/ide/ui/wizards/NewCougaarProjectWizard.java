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


package com.cougaarsoftware.cougaar.ide.ui.wizards;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.NewProjectCreationWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.cougaarsoftware.cougaar.ide.core.CoreMessages;
import com.cougaarsoftware.cougaar.ide.core.IResourceIDs;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUIMessages;


/**
 * Wizard to create a new project with the nature <code>CougaarNature</code>.
 * Processing in this wizard is modeled after the platform new project wizard.
 *
 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard
 */
public class NewCougaarProjectWizard extends NewProjectCreationWizard {
    // switch to control write of trace data
    private boolean traceEnabled = false;
    private CougaarCapabilityConfigurationPage fCougaarPage;

    /**
     * Creates a new NewCougaarJavaProjectCreationWizard object.
     */
    public NewCougaarProjectWizard() {
        super();
        setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWJPRJ);
        setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
        setWindowTitle(CoreMessages.getString("NewCougaarProjectCreationWizard")); //$NON-NLS-1$
    }

    /*
     * @see Wizard#addPages
     */
    public void addPages() {
        super.addPages();
        fCougaarPage = new CougaarCapabilityConfigurationPage();
        fCougaarPage.setTitle(CougaarUIMessages.getString(
                "CougaarCapabilityTitle"));
        fCougaarPage.setDescription(CougaarUIMessages.getString(
                "CougaarCapabilityDescription"));
        addPage(fCougaarPage);
        JavaCapabilityConfigurationPage jcp = (JavaCapabilityConfigurationPage) this
            .getPage("JavaCapabilityConfigurationPage");
        WizardNewProjectCreationPage ncw = (WizardNewProjectCreationPage) this
            .getPage("NewProjectCreationWizard");

        jcp.setTitle(CougaarUIMessages.getString("CougaarCapabilityTitle"));
        jcp.setDescription(CougaarUIMessages.getString(
                "CougaarCapabilityDescription"));

        ncw.setTitle(CougaarUIMessages.getString("CougaarCapabilityTitle"));
        ncw.setDescription(CougaarUIMessages.getString(
                "CougaarCapabilityDescription"));

    }


    /**
     * Customizes the project by adding a builder, the ReadmeBuilder in this
     * scenario.
     *
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure(IJavaProject javaProject)
        throws CoreException {
        System.err.println("About to configure a Cougaar project!!!");

        IPath path = new Path(IResourceIDs.CLASSPATH_CONTAINER_ID);
        IClasspathEntry conEntry = JavaCore.newContainerEntry(path, false);

        IClasspathEntry[] entries = javaProject.getRawClasspath();
        IClasspathEntry[] newentries = new IClasspathEntry[entries.length + 1];
        System.arraycopy(entries, 0, newentries, 0, entries.length);
        newentries[newentries.length - 1] = conEntry;

        IJavaModelStatus validation = JavaConventions.validateClasspath(javaProject,
                newentries, javaProject.getOutputLocation());
        if (!validation.isOK()) {
            throw new CoreException(validation);
        }

        javaProject.setRawClasspath(newentries, null);

    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean performFinish() {
        boolean ret = super.performFinish();
        JavaCapabilityConfigurationPage jcp = (JavaCapabilityConfigurationPage) this
            .getPage("JavaCapabilityConfigurationPage");
        try {
            this.addCustomNature(jcp.getJavaProject().getProject());

            this.configure(jcp.getJavaProject());

        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * Add the nature to the project.
     *
     * @param project DOCUMENT ME!
     *
     * @throws CoreException DOCUMENT ME!
     */
    public void addCustomNature(IProject project)
        throws CoreException {
        try {
            IProjectDescription description = project.getDescription();
            String[] natures = description.getNatureIds();
            String[] newNatures = new String[natures.length + 1];
            System.arraycopy(natures, 0, newNatures, 0, natures.length);
            newNatures[natures.length] = IResourceIDs.COUGAAR_NATURE_ID;
            description.setNatureIds(newNatures);
            project.setDescription(description, null);
        } catch (CoreException e) {
            // ie.- one of the steps resulted in a core exception
            resultError("Create Project with CougaarNature Request",
                "Adding CougaarNature to project " + project.getName()
                + " failed");
            e.printStackTrace();
        }
    }


    /**
     * Used to show action results.
     *
     * @see org.eclipse.jface.dialogs.MessageDialog
     */
    protected void resultInformation(String title, String msg) {
        // Confirm Result
        if (traceEnabled) {
            // trace only to console
            System.out.println(title + msg);
        } else {
            // user interaction response
            MessageDialog.openInformation(getShell(), title, msg);
        }
    }


    /**
     * Used to show action results.
     *
     * @see org.eclipse.jface.dialogs.MessageDialog
     */
    protected void resultError(String title, String msg) {
        // Indicate Error
        if (traceEnabled) {
            // trace only to console
            System.out.println(title + msg);
        } else {
            // user interaction response
            MessageDialog.openError(getShell(), title, msg);
        }
    }
}
