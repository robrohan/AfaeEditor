/*
 * cbg.editor Eclipse plugin
 * Copyright(c) 2002, Chris Grindstaff
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
package com.rohanclan.afae.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.partition.AfaePartitionScanner;
import com.rohanclan.afae.prefs.*;

public class ColorManager {
	/* public static final RGB DEFAULT_STRING_COLOR = new RGB(0, 0, 0);
	public static final RGB DEFAULT_KEYWORD1_COLOR = new RGB(137, 24, 21);
	public static final RGB DEFAULT_KEYWORD2_COLOR = new RGB(102, 102, 153);
	public static final RGB DEFAULT_KEYWORD3_COLOR = new RGB(33, 172, 133);	
	public static final RGB DEFAULT_COMMENT1_COLOR = new RGB(0, 128, 128);
	public static final RGB DEFAULT_COMMENT2_COLOR = new RGB(128, 128, 128);
	public static final RGB DEFAULT_LITERAL1_COLOR = new RGB(0, 0, 255);
	public static final RGB DEFAULT_LITERAL2_COLOR = new RGB(213, 234, 255);
	public static final RGB DEFAULT_LABEL_COLOR = new RGB(255, 128, 0);
	public static final RGB DEFAULT_FUNCTION_COLOR = new RGB(127, 127, 127);	
	public static final RGB DEFAULT_MARKUP_COLOR = new RGB(7, 7, 148);
	public static final RGB DEFAULT_OPERATOR_COLOR = new RGB(102, 102, 153);
	public static final RGB DEFAULT_DIGIT_COLOR = new RGB(255, 0, 0);	
	public static final RGB DEFAULT_INVALID_COLOR = new RGB(255, 128, 128); */

	private Map<String, Color> colorMap;
	private IPreferenceStore store;
	private Map<String, String> typeToColorMap;

	public ColorManager(IPreferenceStore store) {
		colorMap = new HashMap<String, Color>();
		this.store = store;
		initTypeToColorMap();
	}
	
	/**
	 * Creates a new color if that color doesn't already exist in the color map
	 * (handles clean up too)
	 * @param color
	 * @return
	 */
	public Color newColor(RGB color) {
		if(color == null)
			return (Color)colorMap.get(AfaePartitionScanner.NULL);
		
		Color newcolor = null;
		
		if (colorMap.containsKey(color.toString())) {
			newcolor = (Color)colorMap.get(color.toString());
		} else {
			newcolor = new Color(Display.getDefault(), color);
			colorMap.put(color.toString(), newcolor);
		}
		
		return newcolor;
	}
	
	/**
	 * Gets a color from the preference converter. If the color can't be found or
	 * there is an error Null color is used
	 * @param colorName
	 * @return the color
	 * @see AfaePartitionScanner.NULL
	 */
	public Color getColor(String colorName) {
		if(colorName == null)
			return (Color)colorMap.get(AfaePartitionScanner.NULL);
		
		Color color = null;
		try {
			RGB prefColor = PreferenceConverter.getColor(store, colorName);
			
			if (colorMap.containsKey(colorName) && (color = (Color)colorMap.get(colorName)).getRGB().equals(prefColor)) {
				color = (Color)colorMap.get(colorName);
			} else {
				color = new Color(Display.getDefault(), prefColor);
				colorMap.put(colorName, color);
			}
		} catch(NullPointerException e) {
			AfaePlugin.logError("Problem getting color: " + colorName,e, ColorManager.class);
			color = (Color)colorMap.get(AfaePartitionScanner.NULL);
		}
		return color;
	}
	
	protected void initTypeToColorMap() {
		typeToColorMap = new HashMap<String, String>();
		typeToColorMap.put(AfaePartitionScanner.COMMENT1, ColorsPreferencePage.COMMENT1_COLOR);
		typeToColorMap.put(AfaePartitionScanner.COMMENT2, ColorsPreferencePage.COMMENT2_COLOR);
		typeToColorMap.put(AfaePartitionScanner.LITERAL1, ColorsPreferencePage.LITERAL1_COLOR);
		typeToColorMap.put(AfaePartitionScanner.LITERAL2, ColorsPreferencePage.LITERAL2_COLOR);
		typeToColorMap.put(AfaePartitionScanner.LABEL,    ColorsPreferencePage.LABEL_COLOR);
		typeToColorMap.put(AfaePartitionScanner.KEYWORD1, ColorsPreferencePage.KEYWORD1_COLOR);
		typeToColorMap.put(AfaePartitionScanner.KEYWORD2, ColorsPreferencePage.KEYWORD2_COLOR);
		typeToColorMap.put(AfaePartitionScanner.KEYWORD3, ColorsPreferencePage.KEYWORD3_COLOR);
		typeToColorMap.put(AfaePartitionScanner.FUNCTION, ColorsPreferencePage.FUNCTION_COLOR);
		typeToColorMap.put(AfaePartitionScanner.MARKUP,   ColorsPreferencePage.MARKUP_COLOR);
		typeToColorMap.put(AfaePartitionScanner.OPERATOR, ColorsPreferencePage.OPERATOR_COLOR);
		typeToColorMap.put(AfaePartitionScanner.DIGIT,    ColorsPreferencePage.DIGIT_COLOR);
		typeToColorMap.put(AfaePartitionScanner.INVALID,  ColorsPreferencePage.INVALID_COLOR);
		typeToColorMap.put(AfaePartitionScanner.NULL,     ColorsPreferencePage.NULL_COLOR);
	}
	
	public Color getColorForType(String type) {
		String colorName = colorForType(type);
		if(colorName == null) 
			colorName = ColorsPreferencePage.NULL_COLOR;
		
		return getColor(colorName);
	}

	/* public static void initDefaultColors(IPreferenceStore store) {
		PreferenceConverter.setDefault(store, ColorsPreferencePage.NULL_COLOR, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store, ColorsPreferencePage.COMMENT1_COLOR, ColorManager.DEFAULT_COMMENT1_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.COMMENT2_COLOR, ColorManager.DEFAULT_COMMENT2_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.LITERAL1_COLOR, ColorManager.DEFAULT_LITERAL1_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.LITERAL2_COLOR, ColorManager.DEFAULT_LITERAL2_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.LABEL_COLOR, ColorManager.DEFAULT_LABEL_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.KEYWORD1_COLOR, ColorManager.DEFAULT_KEYWORD1_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.KEYWORD2_COLOR, ColorManager.DEFAULT_KEYWORD2_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.KEYWORD3_COLOR, ColorManager.DEFAULT_KEYWORD3_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.FUNCTION_COLOR, ColorManager.DEFAULT_FUNCTION_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.MARKUP_COLOR, ColorManager.DEFAULT_MARKUP_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.OPERATOR_COLOR, ColorManager.DEFAULT_OPERATOR_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.DIGIT_COLOR, ColorManager.DEFAULT_DIGIT_COLOR);
		PreferenceConverter.setDefault(store, ColorsPreferencePage.INVALID_COLOR, ColorManager.DEFAULT_INVALID_COLOR);
		
		String bold = ColorsPreferencePage.BOLD_SUFFIX;
		store.setDefault(ColorsPreferencePage.COMMENT1_COLOR + bold, false);
		store.setDefault(ColorsPreferencePage.COMMENT2_COLOR + bold, true);		
		store.setDefault(ColorsPreferencePage.DIGIT_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.FUNCTION_COLOR + bold,  true);
		store.setDefault(ColorsPreferencePage.INVALID_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.KEYWORD1_COLOR + bold,  true);
		store.setDefault(ColorsPreferencePage.KEYWORD2_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.KEYWORD3_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.LABEL_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.LITERAL1_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.LITERAL2_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.MARKUP_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.OPERATOR_COLOR + bold,  false);
		store.setDefault(ColorsPreferencePage.NULL_COLOR + bold,  false);
	} */

	/**
	 * Checks the color name, and sees if BOLD and/or ITALIC are set on the 
	 * color name, and if so returns an SWT integer that can be passed to a
	 * TextAttribute item.
	 * @param colorName
	 * @return
	 */
	public int getStyleFor(String colorName) {
		
		if(colorName == null) 
			colorName = ColorsPreferencePage.NULL_COLOR;
		
		boolean isBold = store.getBoolean(colorName + IPreferenceConstants.BOLD_SUFFIX);
		boolean isItalic = store.getBoolean(colorName + IPreferenceConstants.ITALIC_SUFFIX);
		
		//System.err.println(isBold + " " + isItalic);
		
		if(!isBold && !isItalic) {
			return SWT.NORMAL;
		} else {
			if(isBold && isItalic) {
				return (SWT.BOLD | SWT.ITALIC);
			} else if(isBold) {
				return SWT.BOLD;
			} else if(isItalic) {
				return SWT.ITALIC;
			}
		}
		
		return SWT.NORMAL;
	}
	
	public int getStyleForType(String type) {
		return getStyleFor(colorForType(type));
	}

	/** 
	 * Converts the ColoringPartitionScanner's type
	 * name to the appropriate preference store color
	 * name.
	 * @param type
	 * @return String
	 */
	public String colorForType(String type) {
		return (String) typeToColorMap.get(type);
	}
	
	public void dispose() {
		Collection colors = colorMap.values();
		for (Iterator iter = colors.iterator(); iter.hasNext();) {
			Color color = (Color) iter.next();
			colorMap.remove(color);
			color.dispose();
		}
		colorMap = null;
	}
}