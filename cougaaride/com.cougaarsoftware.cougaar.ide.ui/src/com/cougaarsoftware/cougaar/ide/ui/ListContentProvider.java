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


import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;


/**
 * A specialized content provider to show a list of cougaar installs
 *
 * @author mabrams
 */
public class ListContentProvider implements IStructuredContentProvider {
    StructuredViewer fViewer;
    List fInput;

    /**
     * Creates a new ListContentProvider object.
     *
     * @param viewer DOCUMENT ME!
     * @param input DOCUMENT ME!
     */
    public ListContentProvider(StructuredViewer viewer, List input) {
        fViewer = viewer;
        fInput = input;
    }

    /**
     * Gets all the elements
     *
     * @param input input
     *
     * @return an array of objects
     */
    public Object[] getElements(Object input) {
        ICougaarInstall[] installs = new ICougaarInstall[fInput.size()];
        return fInput.toArray(installs);
    }


    /**
     * Called when the input to the list has changed
     *
     * @param viewer the viewer that needs to be notified of the change
     * @param oldInput old input
     * @param newInput new input
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        fInput = (List) newInput;
    }


    /**
     * dispose
     */
    public void dispose() {
        fViewer = null;
        fInput = null;
    }


    /**
     * checks to see if an object has been deleted from the list
     *
     * @param o the object to check for deletion
     *
     * @return true if the object has been deleted
     */
    public boolean isDeleted(Object o) {
        return fInput.contains(o);
    }
}
