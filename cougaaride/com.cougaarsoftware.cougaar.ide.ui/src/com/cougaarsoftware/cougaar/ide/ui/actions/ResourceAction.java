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


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.cougaarsoftware.cougaar.ide.ui.CougaarUIPlugin;


/**
 * Common abstract superclass for resource actions. Provides common logic to
 * set the selected resource and display results and errors in a
 * <code>MessageDialog</code>. A no-op stub implementation of the
 * <code>setActivePart</code> method is also provided.
 * 
 * <p>
 * Most actions in this example expect a project as the selected resource, this
 * logic does not care beyond expecting a structured selection.
 * </p>
 */
public abstract class ResourceAction implements IObjectActionDelegate {
    /** Holds selected project resource for run method access */
    public IStructuredSelection selection;
    /** Controls if trace stmts are written */
    boolean traceEnabled = false;

    /* non-Javadoc
     *
     * Not used in this action - implemented per interface.
     *
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }


    /**
     * Used to save a local handle to the selected project resource.
     *
     * @param action action proxy that handles presentation portion of the
     *        plugin action
     * @param selection current selection in the desktop
     *
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction,
     *      ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = (IStructuredSelection) selection;

    }


    /**
     * Uses a <code>MessageDialog</code> to show action results.
     *
     * @see org.eclipse.jface.dialogs.MessageDialog
     */
    protected void resultInformation(String title, String msg) {
        // Confirm Result
        Shell shell = CougaarUIPlugin.getDefault().getWorkbench()
                                     .getActiveWorkbenchWindow().getShell();
        MessageDialog.openInformation(shell, title, msg);
    }


    /**
     * Uses a <code>MessageDialog</code> to show errors in action processing.
     *
     * @see org.eclipse.jface.dialogs.MessageDialog
     */
    protected void resultError(String title, String msg) {
        // Indicate Error
        Shell shell = CougaarUIPlugin.getDefault().getWorkbench()
                                     .getActiveWorkbenchWindow().getShell();
        MessageDialog.openError(shell, title, msg);
    }


    /**
     * Uses a <code>MessageDialog</code> to show errors in action processing.
     *
     * @see org.eclipse.jface.dialogs.MessageDialog
     */
    protected boolean resultQuestion(String title, String msg) {
        // Indicate Error
        Shell shell = CougaarUIPlugin.getDefault().getWorkbench()
                                     .getActiveWorkbenchWindow().getShell();
        return MessageDialog.openQuestion(shell, title, msg);
    }


    /**
     * Write trace statements.
     *
     * @param msg DOCUMENT ME!
     */
    void traceMsg(String msg) {
        if (traceEnabled) {
            System.out.println(msg);
        }
    }
}
