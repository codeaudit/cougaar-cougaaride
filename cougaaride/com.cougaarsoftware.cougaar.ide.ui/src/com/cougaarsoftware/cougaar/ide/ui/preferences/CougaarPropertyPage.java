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

package com.cougaarsoftware.cougaar.ide.ui.preferences;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.ArchiveFileFilter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;


/**
 * DOCUMENT ME!
 *
 * @author Matt Abrams
 */
public class CougaarPropertyPage extends PropertyPage
    implements IStatusChangeListener {
    private CougaarConfigurationBlock fCougaarConfigurationBlock;

    /**
     * Creates a new CougaarPropertyPage object.
     */
    public CougaarPropertyPage() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        setDescription(CougaarPreferencesMessages.getString(
                "CougaarConfigurationPropertyPage.Description")); //$NON-NLS-1$

        super.createControl(parent);
    }


    /*
     * @see org.eclipse.jface.preference.IPreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        fCougaarConfigurationBlock = new CougaarConfigurationBlock(this, getProject());
        Control control = fCougaarConfigurationBlock.createContents(parent);

        control.setVisible(true);
        Dialog.applyDialogFont(control);
        return control;
    }


    /*
     * @see PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        fCougaarConfigurationBlock.performDefaults();
        super.performDefaults();
    }


    /**
     * @see IStatusChangeListener#statusChanged(IStatus)
     */
    public void statusChanged(IStatus status) {
        setValid(!status.matches(IStatus.ERROR));
        StatusUtil.applyToStatusLine(this, status);
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean performOk() {
        //TODO show error messages when things go wrong. (where the return false is)
        String cougaarVersion = fCougaarConfigurationBlock.getCougaarVersion();
        CougaarPlugin.getDefault().getPreferenceStore().setValue(CougaarPlugin.DEFAULT_COUGAAR_PREFERENCE,
            cougaarVersion);

        CougaarPlugin.savePreference(ICougaarConstants.COUGAAR_VERSION, cougaarVersion,
             getProject());
        if (cougaarVersion == null) {
            return false;
        }

        try {
            CougaarPlugin.updateClasspathContainer(getJavaElement()
                                                       .getJavaProject());
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }

        CougaarPlugin.getDefault().savePluginSettings();

        return super.performOk();
    }


	/**
	 * Method getProject.
	 *
	 * @return DOCUMENT ME!
	 */
	private IProject getProject() {
		return (IProject) getElement().getAdapter(IProject.class);
	}
	


    private IJavaElement getJavaElement() {
        IAdaptable adaptable = getElement();
        IJavaElement elem = (IJavaElement) adaptable.getAdapter(IJavaElement.class);
        if (elem == null) {
            IResource resource = (IResource) adaptable.getAdapter(IResource.class);

            //special case when the .jar is a file
            try {
                if (resource instanceof IFile
                    && ArchiveFileFilter.isArchivePath(resource.getFullPath())) {
                    IProject proj = resource.getProject();
                    if (proj.hasNature(JavaCore.NATURE_ID)) {
                        IJavaProject jproject = JavaCore.create(proj);
                        elem = jproject.getPackageFragmentRoot(resource); // create a handle
                    }
                }
            } catch (CoreException e) {
                JavaPlugin.log(e);
            }
        }

        return elem;
    }
}
