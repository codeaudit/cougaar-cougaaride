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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUIPlugin;


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
        try {
            List input = new ArrayList();
            input.add(CougaarPlugin.getAllCougaarLocations().values().iterator());
            //ask for cougaar version in a dialog
            InputDialog dlg = new InputDialog(CougaarUIPlugin.getDefault()
                                                                             .getWorkbench()
                                                                             .getActiveWorkbenchWindow()
                                                                             .getShell(),
            "Cougaar Version", "Select the Cougaar Version to use.", "",null);
            dlg.open();

            if (dlg.getReturnCode() != Window.OK) {
                return;
            }
            String version = dlg.getValue();


            CougaarPlugin.savePreference(ICougaarConstants.COUGAAR_VERSION,
                version, jproject.getProject());

            CougaarPlugin.convertToCougaarProject(jproject, null);
        } catch (CoreException e) {
            resultError("Error!", "Problems converting to Cougaar Project!");
            return;
        }
    }
}
