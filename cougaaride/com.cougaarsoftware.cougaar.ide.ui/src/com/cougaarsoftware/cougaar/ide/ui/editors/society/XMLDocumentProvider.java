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


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class XMLDocumentProvider extends FileDocumentProvider {
    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CoreException DOCUMENT ME!
     */
    protected IDocument createDocument(Object element)
        throws CoreException {
        IDocument document = super.createDocument(element);
        if (document != null) {
            IDocumentPartitioner partitioner = new DefaultPartitioner(new XMLPartitionScanner(),
                    new String[] {
                        XMLPartitionScanner.XML_TAG,
                        XMLPartitionScanner.XML_COMMENT
                    });
            partitioner.connect(document);
            document.setDocumentPartitioner(partitioner);
        }

        return document;
    }
}
