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


/**
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials  are made available under the
 * terms of the Common Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/cpl-v10.html
 * Contributors: IBM Corporation - initial API and implementation
 */
package com.cougaarsoftware.cougaar.ide.ui.editors.society;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ColorManager {
    protected Map fColorTable = new HashMap(10);

    /**
     * DOCUMENT ME!
     */
    public void dispose() {
        Iterator e = fColorTable.values().iterator();
        while (e.hasNext()) {
            ((Color) e.next()).dispose();
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param rgb DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getColor(RGB rgb) {
        Color color = (Color) fColorTable.get(rgb);
        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
            fColorTable.put(rgb, color);
        }

        return color;
    }
}
