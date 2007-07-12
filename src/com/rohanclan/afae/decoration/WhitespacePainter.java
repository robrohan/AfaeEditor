package com.rohanclan.afae.decoration;
/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *    Sebastian Davids <sdavids@gmx.de> - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeSourceViewer;
import com.rohanclan.afae.prefs.IPreferenceConstants;

/**
 * Decorates the peer character if it is a whitespace.
 * Clients instantiate and configure object of this class.
 * 
 * @since 3.0
 */
public final class WhitespacePainter implements IPainter, PaintListener {
	
	/** Indicates whether this painter is active */
	private boolean fIsActive= false;
	/** The source viewer this painter is associated with */
	private ISourceViewer fSourceViewer;
	/** The viewer's widget */
	private StyledText fTextWidget;
	
	/** The color in which to draw the whitespace characters */
	private Color fColor;

	/** The decorator for space characters */
	private String fSpaceCharacter;
	/** The decorator for tab characters */
	private String fTabCharacter;
	/** The decorator for EOL characters */
	private String fEOLCharacter;
	
	/** The EOL char used by the system */
	//private char fSystemEOLChar;
	
	/**
	 * Creates a new WhitespacePainter for the given source viewer.
	 * 
	 * @param sourceViewer the source viewer
	 */
	public WhitespacePainter(ISourceViewer sourceViewer) {
		fSourceViewer = sourceViewer;
		fTextWidget = sourceViewer.getTextWidget();
		
		fSpaceCharacter = String.valueOf((char) 183); //middot
		fTabCharacter = String.valueOf((char) 187); // >>
		fEOLCharacter = String.valueOf((char) 172); //(char) 182); // paragraph sign
		
		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
		fColor = new org.eclipse.swt.graphics.Color(
			Display.getCurrent(),
			StringConverter.asRGB(store.getString(IPreferenceConstants.LABEL_COLOR))
		);
		
		//String eol = System.getProperty("line.separator"); //$NON-NLS-1$
		/*if ((eol != null) && (eol.length() > 0))
			fSystemEOLChar = eol.charAt(0);
		else
			fSystemEOLChar = '\n';
		*/
	}
	
	/*
	 * @see org.eclipse.jface.text.IPainter#deactivate(boolean)
	 */
	public void deactivate(boolean redraw) {
		if (fIsActive) {
			fIsActive = false;
			fTextWidget.removePaintListener(this);
			if (redraw)
				handleDrawRequest(null);
		}
	}

	/*
	 * @see org.eclipse.jface.text.IPainter#dispose()
	 */
	public void dispose() {
		fColor = null;
		fTextWidget = null;
	}

	/**
	 * Decorates the given whitespace.
	 * 
	 * @param gc the gc to draw into
	 * @param offset the offset of the whitespace
	 * @param whitespace the decorator for this whitespace
	 */
	private void draw(GC gc, int offset, String whitespace, int extraX) 
	{
		Point point= fTextWidget.getLocationAtOffset(offset);
		
		if(fColor != null)
			gc.setForeground(fColor);
		
		gc.drawText(whitespace, (extraX + point.x), point.y, true);
	}

	/**
	 * Handles a redraw request.
	 * 
	 * @param gc the gc to draw into.
	 */
	private void handleDrawRequest(GC gc) {
		if (gc == null) {
			//System.err.println("gc is null");
			fTextWidget.redraw();
			return;
		}

		try {
			//fTextWidget.redraw();
			
			IRegion region = ((AfaeSourceViewer)fSourceViewer).getRealVisibleRegion();
		
			int offset = region.getOffset(); 
			int len = region.getLength();
						
			String visibleText = fSourceViewer.getDocument().get(offset, len);

			//for(int i = 0; i < len; ++i)
			for(int i=(len - 1); i > 0; i--)
			{
				char c = visibleText.charAt(i);
				
				switch(c)
				{
					case ' ':
						draw(gc, (offset+i), fSpaceCharacter,0);
					break;
					
					case '\t':
						draw(gc, (offset+i), fTabCharacter,0);
					break;
					
					case '\n':
					case '\r':
						draw(gc, (offset+i), fEOLCharacter,3);
					break; 
				}
				
				/* if (c == ' ')
					draw(gc, (offset+i), fSpaceCharacter);
				else if (c == '\t')
					draw(gc, (offset+i), fTabCharacter);
				else if (c == fSystemEOLChar)
					draw(gc, (offset+i), fEOLCharacter);
				*/					
			}
			
			//draw EOL at EOF
			//draw(gc, (offset+len), fEOLCharacter);
		} catch (BadLocationException e) {
			return;
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.IPainter#paint(int)
	 */
	public void paint(int reason) {
		IDocument document= fSourceViewer.getDocument();
		if (document == null) {
			deactivate(false);
			return;
		}
	
		if (fIsActive) {
			if ((IPainter.CONFIGURATION == reason) || (IPainter.TEXT_CHANGE == reason))
				handleDrawRequest(null);
		} else {
			fIsActive= true;
			fTextWidget.addPaintListener(this);
			handleDrawRequest(null);
		}
	}

	/*
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent event) {
		if (fTextWidget != null)
		{
			if(event.gc != null)
			{
				handleDrawRequest(event.gc);
			}
		}
	}

	/**
	 * Sets the color in which to draw whitespaces.
	 * 
	 * @param color the color
	 */
	public void setColor(Color color) {
		fColor= color;
	}

	/*
	 * @see org.eclipse.jface.text.IPainter#setPositionManager(org.eclipse.jface.text.IPaintPositionManager)
	 */
	public void setPositionManager(IPaintPositionManager manager) {}
}
