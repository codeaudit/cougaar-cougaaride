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


package com.cougaarsoftware.cougaar.ide.ui.actions;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUIPlugin;
import com.cougaarsoftware.cougaar.ide.ui.dialogs.SelectCougaarInstallDialog;


/**
 * DOCUMENT ME!
 *
 * @author soster
 */
public class ConvertToCougaarProjectAction extends ResourceAction {
    /**
     * DOCUMENT ME!
     *
     * @param action DOCUMENT ME!
     */
    public void run(IAction action) {
        Object selectedObject = selection.getFirstElement();

        if (!(selectedObject instanceof IJavaProject)) {
            resultError("Error!", "Selected project must be a Java Project!");
            return;
        }

        IJavaProject jproject = (IJavaProject) selectedObject;
        Shell shell = CougaarUIPlugin.getDefault().getWorkbench()
                                     .getActiveWorkbenchWindow().getShell();
        SelectCougaarInstallDialog dlg = new SelectCougaarInstallDialog(shell,
                jproject.getProject());
        dlg.open();

        if (dlg.getReturnCode() != Window.OK) {
            return;
        }

        String version = dlg.getValue();
        if ((version == null) || version.trim().equals("")) {
            resultError("Error!", "Empty or invalid Cougaar version selected!");
            return;
        }

        CougaarPlugin.savePreference(ICougaarConstants.COUGAAR_VERSION,
            version, jproject.getProject());

        try {
            IRunnableWithProgress op = new CougaarConversionWithProgress(jproject);
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
            IProgressMonitor monitor = dialog.getProgressMonitor();
            monitor.setTaskName("Converting project to Cougaar project.");
            dialog.run(true, true, op);
        } catch (InvocationTargetException e) {
            resultError("Error!", "Problems converting to Cougaar Project!");
            return;
        } catch (InterruptedException e) {
            resultInformation("Conversion Cancelled",
                "Converstion to Cougaar project cancelled, please verify the classpath is still correct.");
            return;
        }
    }

    /**
     * Used to do the work of conversion in a thread
     *
     * @author soster
     */
    private class CougaarConversionWithProgress implements IRunnableWithProgress {
        private IJavaProject jproject;

        public CougaarConversionWithProgress(IJavaProject jproject) {
            this.jproject = jproject;
        }

        public void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {
            try {
                CougaarPlugin.convertToCougaarProject(jproject, monitor);
            } catch (CoreException e) {
                throw new InvocationTargetException(e);
            }
        }
    }
}
