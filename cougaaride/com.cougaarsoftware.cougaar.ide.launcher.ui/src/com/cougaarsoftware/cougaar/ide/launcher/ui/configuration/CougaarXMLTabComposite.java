package com.cougaarsoftware.cougaar.ide.launcher.ui.configuration;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class CougaarXMLTabComposite extends Composite {

	public CougaarXMLTabComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		setSize(new Point(300, 200));
		setLayout(new GridLayout());
	}

}
