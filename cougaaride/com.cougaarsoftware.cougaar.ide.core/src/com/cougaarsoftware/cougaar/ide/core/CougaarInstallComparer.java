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


import org.eclipse.jface.viewers.IElementComparer;


/**
 * Compares two <code>CougaarInstall</code> objects
 *
 * @author mabrams
 */
public class CougaarInstallComparer implements IElementComparer {
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object a, Object b) {
        return a.equals(b);
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
     */
    public int hashCode(Object element) {
        return element.hashCode();
    }
}
