/*
 * <copyright>
 *  Copyright 2000-2003 Cougaar Software, Inc.
 *  All Rights Reserved
 * </copyright>
 */


package com.cougaarsoftware.cougaar.ide.ui.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;


/**
 * Uses the Eclipse <code>Browser</code> to display a view with the cougaar
 * society viewer servlet within the eclipse enviornement.  NOTE:  The
 * Browswer is a native widget and is only supported on: Windows (using
 * Internet Explorer 5.0 and above) and  Linux GTK (using Mozilla 1.4 GTK2).
 *
 * @author mabrams
 */
public class SocietyViewer extends ViewPart {
    /**
     * Create the Browser component with navigation buttons.  opens the page:
     * http://localhost:8800
     *
     * @param parent DOCUMENT ME!
     */
    public void createPartControl(Composite parent) {
        Composite comp = parent;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        comp.setLayout(gridLayout);
        ToolBar toolbar = new ToolBar(comp, SWT.NONE);
        ToolItem itemBack = new ToolItem(toolbar, SWT.PUSH);
        itemBack.setText("Back");
        ToolItem itemForward = new ToolItem(toolbar, SWT.PUSH);
        itemForward.setText("Forward");
        ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
        itemStop.setText("Stop");
        ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
        itemRefresh.setText("Refresh");
        ToolItem itemGo = new ToolItem(toolbar, SWT.PUSH);
        itemGo.setText("Go");


        GridData data = new GridData();
        data.horizontalSpan = 3;
        toolbar.setLayoutData(data);

        Label labelAddress = new Label(comp, SWT.NONE);
        labelAddress.setText("Address");

        final Text location = new Text(comp, SWT.BORDER);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        location.setLayoutData(data);


        final Browser browser = new Browser(comp, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 3;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        browser.setLayoutData(data);

        final Label status = new Label(comp, SWT.NONE);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        status.setLayoutData(data);

        final ProgressBar progressBar = new ProgressBar(comp, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.END;
        progressBar.setLayoutData(data);

        /* event handling */
        Listener listener = new Listener() {
                public void handleEvent(Event event) {
                    ToolItem item = (ToolItem) event.widget;
                    String string = item.getText();
                    if (string.equals("Back")) {
                        browser.back();
                    } else if (string.equals("Forward")) {
                        browser.forward();
                    } else if (string.equals("Stop")) {
                        browser.stop();
                    } else if (string.equals("Refresh")) {
                        browser.refresh();
                    } else if (string.equals("Go")) {
                        browser.setUrl(location.getText());
                    }
                }
            };

        browser.addProgressListener(new ProgressListener() {
                public void changed(ProgressEvent event) {
                    if (event.total == 0) {
                        return;
                    }

                    int ratio = (event.current * 100) / event.total;
                    progressBar.setSelection(ratio);
                }


                public void completed(ProgressEvent event) {
                    progressBar.setSelection(0);
                }
            });
        browser.addStatusTextListener(new StatusTextListener() {
                public void changed(StatusTextEvent event) {
                    status.setText(event.text);
                }
            });

        browser.addLocationListener(new LocationListener() {
                public void changed(LocationEvent event) {
                    location.setText(event.location);
                }


                public void changing(LocationEvent event) {
                }
            });

        itemBack.addListener(SWT.Selection, listener);
        itemForward.addListener(SWT.Selection, listener);
        itemStop.addListener(SWT.Selection, listener);
        itemRefresh.addListener(SWT.Selection, listener);
        itemGo.addListener(SWT.Selection, listener);
        location.addListener(SWT.DefaultSelection,
            new Listener() {
                public void handleEvent(Event e) {
                    browser.setUrl(location.getText());
                }
            });

        browser.setUrl("http://localhost:8800/");

    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
    }
}
