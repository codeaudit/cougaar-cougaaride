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


import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CougaarPerspectiveFactory implements IPerspectiveFactory {
    /**
     * Creates a new CougaarPerspectiveFactory object.
     */
    public CougaarPerspectiveFactory() {
        super();
    }

    /**
     * DOCUMENT ME!
     *
     * @param layout DOCUMENT ME!
     */
    public void createInitialLayout(IPageLayout layout) {
        //		// Add "show views".
        //		   layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
        //		   layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
        //
        //		// Editors are placed for free.
        //		String editorArea = layout.getEditorArea();
        //
        //		// Place navigator and outline to right of
        //		// editor area.
        //		IFolderLayout right =
        //				layout.createFolder("right", IPageLayout.RIGHT, (float) 0.5, editorArea);
        //		right.addView(IPageLayout.ID_RES_NAV);
        //		right.addView(IPageLayout.ID_OUTLINE);
    }
}
