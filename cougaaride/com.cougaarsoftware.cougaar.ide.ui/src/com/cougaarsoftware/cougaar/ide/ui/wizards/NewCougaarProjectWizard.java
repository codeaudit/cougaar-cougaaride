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


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.cougaarsoftware.cougaar.ide.core.CoreMessages;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUIMessages;


/**
 * Wizard to create a new project with the nature <code>CougaarNature</code>.
 * Processing in this wizard is modeled after the platform new project wizard.
 *
 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard
 */
public class NewCougaarProjectWizard extends JavaProjectWizard {
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
        //add the page to prompt for the cougaar version
        fCougaarPage = new CougaarCapabilityConfigurationPage(this);
        fCougaarPage.setTitle(CougaarUIMessages.getString(
                "CougaarCapabilityTitle"));
        fCougaarPage.setDescription(CougaarUIMessages.getString(
                "CougaarCapabilityDescription"));
        addPage(fCougaarPage);

        //add the java pages
        super.addPages();

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
     *
     */
    public boolean performFinish() {
        //let java finish first (need project to be setup)
        boolean superReturn = super.performFinish();
        if (superReturn) {
            IWorkspaceRunnable op = new IWorkspaceRunnable() {
                    public void run(IProgressMonitor monitor)
                        throws CoreException, OperationCanceledException {
                        try {
                            fCougaarPage.finishPage(monitor);
                        } catch (InterruptedException e) {
                            throw new OperationCanceledException(e.getMessage());
                        }
                    }
                };

            try {
                getContainer().run(false, true, new WorkbenchRunnableAdapter(op));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;

        } else {
            return false;
        }
    }
}
