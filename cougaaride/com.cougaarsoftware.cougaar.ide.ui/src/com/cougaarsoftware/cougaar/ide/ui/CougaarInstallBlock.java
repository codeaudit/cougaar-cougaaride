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


import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;


/**
 * A dialog block for cougaar installations
 *
 * @author mabrams
 */
public class CougaarInstallBlock {
    protected Button fDefaultButton;
    protected File cougaarInstallPath;

    /**
     * the constructor
     *
     * @param dialog
     */
    public CougaarInstallBlock(AddCougaarDialog dialog) {
        // TODO Auto-generated constructor stub
    }

    /**
     * Set the home directory
     *
     * @param file
     */
    public void setHomeDirectory(File file) {
        cougaarInstallPath = file;
    }


    /**
     * get the home directory
     *
     * @return DOCUMENT ME!
     */
    public File getHomeDirectory() {
        return cougaarInstallPath;
    }


    /**
     *
     */
    public void update() {
        // TODO Auto-generated method stub
    }


    /**
     * create the install control
     *
     * @param parent
     *
     * @return the control
     */
    public Control createControl(Composite parent) {
        Font font = parent.getFont();

        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 2;
        comp.setLayout(topLayout);
        GridData gd = new GridData(GridData.FILL_BOTH);
        comp.setLayoutData(gd);


        Composite pathButtonComp = new Composite(comp, SWT.NONE);
        GridLayout pathButtonLayout = new GridLayout();
        pathButtonLayout.marginHeight = 0;
        pathButtonLayout.marginWidth = 0;
        pathButtonComp.setLayout(pathButtonLayout);
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                | GridData.HORIZONTAL_ALIGN_FILL);
        pathButtonComp.setLayoutData(gd);
        pathButtonComp.setFont(font);

        createVerticalSpacer(comp, 2);


        return comp;

    }


    /**
     * initialize from (does nothing for now)
     *
     * @param object
     */
    public void initializeFrom(Object object) {
        // TODO Auto-generated method stub
    }


    /**
     * Create some empty space
     *
     * @param comp DOCUMENT ME!
     * @param colSpan DOCUMENT ME!
     */
    protected void createVerticalSpacer(Composite comp, int colSpan) {
        Label label = new Label(comp, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = colSpan;
        label.setLayoutData(gd);
    }


    /**
     * perform apply, does nothing for now.
     *
     * @param cougaar
     */
    public void performApply(ICougaarInstall cougaar) {
        // TODO Auto-generated method stub
    }
}
