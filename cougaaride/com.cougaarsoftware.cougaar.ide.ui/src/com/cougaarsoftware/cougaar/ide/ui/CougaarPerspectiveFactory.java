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


package com.cougaarsoftware.cougaar.ide.ui;


import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * Creates the Cougaar Perspective
 *
 * @author soster, mabrams
 */
public class CougaarPerspectiveFactory implements IPerspectiveFactory {
    /** the id for the cougaar society viewer component */
    public static String ID_COUGAAR_SOCIETY = "com.cougaarsoftware.cougaar.ide.ui.views.SocietyViewer";

    /**
     * Creates a new CougaarPerspectiveFactory object.
     */
    public CougaarPerspectiveFactory() {
        super();
    }

    /**
     * Arrange the perspective defaults; for now mostly yanked from the Java
     * Perspective
     *
     * @param layout the default layout
     */
    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();

        IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT,
                (float) 0.25, editorArea);
        folder.addView(JavaUI.ID_PACKAGES);
        folder.addView(JavaUI.ID_TYPE_HIERARCHY);
        folder.addPlaceholder(IPageLayout.ID_RES_NAV);
        folder.addView(CougaarPerspectiveFactory.ID_COUGAAR_SOCIETY);

        IFolderLayout outputfolder = layout.createFolder("bottom",
                IPageLayout.BOTTOM, (float) 0.75, editorArea);
        outputfolder.addView(IPageLayout.ID_TASK_LIST);


        //layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float) 0.75, editorArea);
        // views - java
        layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
        layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);

        //TODO put cougaar views here
        // views - standard workbench
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(CougaarPerspectiveFactory.ID_COUGAAR_SOCIETY);

        //TODO change this to stuff like "New Agent.." etc
        // new actions - Cougaar project creation wizard
        layout.addNewWizardShortcut(
            "com.cougaarsoftware.cougaar.ide.ui.wizards.CougaarProject");

    }
}
