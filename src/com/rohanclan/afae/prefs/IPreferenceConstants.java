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
package com.rohanclan.afae.prefs;

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

public interface IPreferenceConstants {
	/** color for the default text */
	public static final String NULL_COLOR = "nullColor";

	/** color for the comment text */
	public static final String COMMENT1_COLOR = "comment1Color";

	/** color for the comment text */
	public static final String COMMENT2_COLOR = "comment2Color";

	/** color for the literal text */
	public static final String LITERAL1_COLOR = "literal1Color";

	/** color for the literal text */
	public static final String LITERAL2_COLOR = "literal2Color";

	/** color for the label text */
	public static final String LABEL_COLOR = "labelColor";

	/** color for the keyword text */
	public static final String KEYWORD1_COLOR = "keyword1Color";

	/** color for the keyword text */
	public static final String KEYWORD2_COLOR = "keyword2Color";

	/** color for the keyword text */
	public static final String KEYWORD3_COLOR = "keyword3Color";

	/** color for the function text */
	public static final String FUNCTION_COLOR = "functionColor";

	/** color for the markup text */
	public static final String MARKUP_COLOR = "markupColor";

	/** color for the operator text */
	public static final String OPERATOR_COLOR = "operatorColor";

	/** color for the digit text */
	public static final String DIGIT_COLOR = "digitColor";

	/** color for the invalid text */
	public static final String INVALID_COLOR = "invalidColor";
	
	
	public static final String[] ALL_TEXT_COLORS = {
		COMMENT1_COLOR, COMMENT2_COLOR, LITERAL1_COLOR, LITERAL2_COLOR,
		LABEL_COLOR, KEYWORD1_COLOR, KEYWORD2_COLOR, KEYWORD3_COLOR, FUNCTION_COLOR,
		MARKUP_COLOR, OPERATOR_COLOR, DIGIT_COLOR
	};
	

	/**
	 * the bold suffix applied to one of the color keys to see if it should be
	 * bolded
	 */
	public static final String BOLD_SUFFIX = "Bold";
	
	/**
	 * the bold suffix applied to one of the color keys to see if it should be
	 * italic
	 */
	public static final String ITALIC_SUFFIX = "Italic";
	/**
	 * the strike suffix applied to one of the color keys to see if it should be
	 * strike though
	 */
	public static final String STRIKE_SUFFIX = "Strike";
	/**
	 * the underline suffix applied to one of the color keys to see if it should be
	 * underlined
	 */
	public static final String UNDERLINE_SUFFIX = "Underline";
	
	/**
	 * The new style for the properties that uses the 
	 * boolean,boolean,boolean,boolean format to style text rules
	 */
	public static final String TEXT_PROPERTY_SUFFIX = "Props";
	
	
	public static final String BACKGROUND_COLOR_SUFFIX ="BG";
	
	////////////////////////////////////////////////////////////////////////////////////
	
	/** Preference key for inserting spaces rather than tabs. */
	public static final String SPACES_FOR_TABS = "spacesForTabs"; //$NON-NLS-1$

	/** Preference key identifier for showing the line number ruler */
	public static final String P_SHOW_LINE_NUMBERS = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER;

	public static final String P_SHOW_WHITESPACE = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SHOW_WHITESPACE_CHARACTERS;
	/* public static final String P_SHOW_WHITESPACE = "com.rohanclan.afae.ShowWhitespacee"; */

	/** Preference key identifier for line number color */
	public static final String P_LINE_NUMBER_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER_COLOR;

	/** Preference key identifier for current line highlight color */
	public static final String P_CURRENT_LINE_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR;

	/** Preference key identifier for print margin size */
	public static final String P_PRINT_MARGIN_SIZE = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN;

	/** Preference key identifier for showing print margin */
	public static final String P_SHOW_PRINT_MARGIN = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN;

	/** Preference key identifier for print margin color */
	public static final String P_PRINT_MARGIN_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR;

	/** Preference key identifier for showing the overview ruler */
	public static final String P_SHOW_OVERVIEW_RULER = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_OVERVIEW_RULER;

	/** Preference key identifier for highlighting the line the cursor is on */
	public static final String P_HIGHLIGHT_CURRENT_LINE = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE;

	/** Preference key identifier for enabling custom carets */
	public static final String P_ENABLE_CUSTOM_CARETS = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_USE_CUSTOM_CARETS;

	/** Preference key identifier for using a wide caret */
	public static final String P_USE_WIDE_CARET = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_WIDE_CARET;

	/** Used to Toggle word wrap */
	public static final String P_USE_WORD_WRAP = "afae.editor.wordwrap";
	
	
	/**
	 * Preference key identifier for using the system default for the selection
	 * foreground color
	 */
	public static final String P_SELECTION_FOREGROUND_SYSTEM_DEFAULT = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR;

	/**
	 * Preference key identifier for using the system default for the selection
	 * background color
	 */
	public static final String P_SELECTION_BACKGROUND_SYSTEM_DEFAULT = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR;

	/** Preference key identifier for turning on hyperlinks in the editor */
	// public static final String P_EDITOR_HYPERLINKS_ENABLED =
	// AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED;
	/**
	 * Preference key identifier for the key to press when following a hyperlink
	 * in the editor
	 */
	// public static final String P_EDITOR_HYPERLINK_KEY_MODIFIER =
	// AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINK_KEY_MODIFIER;
	public static final String P_BRACKET_MATCHING_ENABLED = "com.rohanclan.afae.bracket.matching.enabled";

	public static final String P_BRACKET_MATCHING_COLOR = "com.rohanclan.afae.bracket.matching.color";

	/** Style constant for bracket matching as an outline */
	public static final int BRACKET_MATCHING_OUTLINE = 0;

	/** Style constant for bracket matching as a background color */
	public static final int BRACKET_MATCHING_BACKGROUND = 1;

	/** Style constant for bracket matching as bold text */
	public static final int BRACKET_MATCHING_BOLD = 2;

	/** preference key identifier for content assist delay */
	public static final String P_INSIGHT_DELAY = "afae.editor.insightDelay";

	/** Preference key identifier for insert spaces instead of tabs */
	public static final String P_INSERT_SPACES_FOR_TABS = "afae.editor.tabsAsSpaces";

	/** Preference key identifier for tab width */
	public static final String P_TAB_WIDTH = "afae.editor.tabWidth";

	/** Preference key identifier for max undo steps */
	public static final String P_MAX_UNDO_STEPS = "afae.editor.maxUndoSteps";

	/**
	 * Preference key identifier for whether or not tab indents single line if
	 * part of the line is selected
	 */
	public static final String P_TAB_INDENTS_CURRENT_LINE = "afae.editor.tabIndentsCurrentLine";

	/** Preference key identifier for trimming trailing spaces when you save */
	//public static final String P_RTRIM_ON_SAVE = "afae.editor.rTrimOnSave";

	/** Preference key identifier for selection foreground color */
	public static final String P_SELECTION_FOREGROUND_COLOR = "AbstractTextEditor.Color.SelectionForeground";

	/** Preference key identifier for selection background color */
	public static final String P_SELECTION_BACKGROUND_COLOR = "AbstractTextEditor.Color.SelectionBackground";

	/** Preference key identifier for the text editor background color */
	public static final String P_COLOR_BACKGROUND = "AbstractTextEditor.Color.Background";

	/** Preference key identifier for warning when opening read only files */
	public static final String P_WARN_READ_ONLY_FILES = "afae.editor.warnReadOnlyFiles";

	/** Preference key identifier for bracket matching style */
	public static final String P_BRACKET_MATCHING_STYLE = "afae.editor.bracketMatchingStyle";
}
