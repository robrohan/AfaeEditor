/*
 * Copyright (c) 2005 Rob Rohan 
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.rohanclan.afae.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

public class AfaeSourceViewer extends SourceViewer {

	public AfaeSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean showAnnotationsOverview,	int styles) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
	}

	/**
	 * 
	 * @return
	 */
	protected int getStartVisibleArea() {
		try {
			int possibletop = getTopIndexStartOffset() - 10;
			int top = (possibletop < 0) ? 0 : possibletop;

			return top;
		} catch (java.lang.NullPointerException npe) {
			return 0;
		}
	}

	/**
	 * 
	 * @return
	 */
	protected int getLengthVisibleArea() {
		try {
			IDocument document = getVisibleDocument();
			int possiblebottom = ((getBottomIndexEndOffset() + 10) - getTopIndexStartOffset());
			int bottom = (possiblebottom > document.getLength()) ? document.getLength() : possiblebottom;
			return bottom;
		} catch (java.lang.NullPointerException npe) {
			return 0;
		}
	}

	/*
	 * 
	 */
	public IRegion getRealVisibleRegion() {
		return new Region(getStartVisibleArea(), getLengthVisibleArea());
	}

	/**
	 * repaint the visible area of text (not the whole document area)
	 */
	public void quickRedraw() {
		this.getTextWidget().redrawRange(getStartVisibleArea(), getLengthVisibleArea(), true);
	}
}
