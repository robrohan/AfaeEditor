/*
 * Created on Sep 9, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Stephen Milligan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package com.rohanclan.afae.editor;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * @author Stephen Milligan This class handles the hover text for source viewer
 *         markers.
 */
public class AfaeAnnotationHover implements IAnnotationHover {
	private static final int MAXCOLUMNLEN = 100;

	private static final int MAXLINES = 7;

	/**
	 * 
	 */
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		String[] messages = getMessagesForLine(sourceViewer, lineNumber);

		if (messages.length == 0)
			return null;

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < messages.length; i++) {
			buffer.append(messages[i]);
			if (i < messages.length - 1)
				buffer.append(System.getProperty("line.separator")); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @param viewer
	 * @param line
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String[] getMessagesForLine(ISourceViewer viewer, int line) {
		IDocument document = viewer.getDocument();
		IAnnotationModel model = viewer.getAnnotationModel();

		if (model == null)
			return new String[0];

		ArrayList<String> messages = new ArrayList<String>();

		int errnum = 0;

		Iterator iter = model.getAnnotationIterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof MarkerAnnotation) {
				MarkerAnnotation annotation = (MarkerAnnotation) object;
				if (compareRulerLine(model.getPosition(annotation), document,
						line)) {
					IMarker marker = annotation.getMarker();
					String message = marker.getAttribute(IMarker.MESSAGE,
							"::unk::");

					if (message != null && message.trim().length() > 0) {
						messages.add(++errnum + ") "
								+ wrapLongText(message.trim()));
					}
				}
			}
		}
		return (String[]) messages.toArray(new String[messages.size()]);
	}

	/**
	 * Makes sure text can fit in the window somewhat (xml errors tend to be
	 * really crazy gt 3000 chars of text for example
	 * 
	 * @param errortext
	 * @return
	 */
	private String wrapLongText(String errortext) {
		if (errortext.length() <= MAXCOLUMNLEN)
			return errortext;

		StringBuffer textwithnewline = new StringBuffer();

		int startpos = 0;
		int currentline = 0;
		int maxlooplines = errortext.length() / MAXCOLUMNLEN;

		for (int x = 0; x < maxlooplines; x++) {
			if (((startpos + MAXCOLUMNLEN) > errortext.length())
					|| startpos > errortext.length())
				break;

			textwithnewline.append(errortext.substring(startpos,
					(startpos + MAXCOLUMNLEN)));

			startpos += MAXCOLUMNLEN;
			currentline++;
			// if there are just too many lines break out
			if (currentline >= MAXLINES) {
				textwithnewline.append("\n[[more]] ...");
				break;
			}

			textwithnewline.append("\n");
		}

		return textwithnewline.toString();
	}

	/**
	 * 
	 * @param position
	 * @param document
	 * @param line
	 * @return
	 */
	private boolean compareRulerLine(Position position, IDocument document,
			int line) {
		try {
			if (position.getOffset() > -1 && position.getLength() > -1) {
				return document.getLineOfOffset(position.getOffset()) == line;
			}
		} catch (BadLocationException e) {
		}
		return false;
	}
}