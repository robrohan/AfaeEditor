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

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.rohanclan.afae.AfaePlugin;

public class ColorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, IPreferenceConstants {

	public ColorsPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(AfaePlugin.getDefault().getPreferenceStore());
	}

	protected void createFieldEditors() {
		Composite p = getFieldEditorParent();
		String b = "&Bold";

		addField(new BooleanColorFieldEditor(NULL_COLOR, "Default Text", NULL_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(KEYWORD1_COLOR, "Keyword 1", KEYWORD1_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(KEYWORD2_COLOR, "Keyword 2", KEYWORD2_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(KEYWORD3_COLOR, "Keyword 3", KEYWORD3_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(COMMENT1_COLOR, "Comment 1", COMMENT1_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(COMMENT2_COLOR, "Comment 2", COMMENT2_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(LITERAL1_COLOR, "Literal 1", LITERAL1_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(LITERAL2_COLOR, "Literal 2", LITERAL2_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(LABEL_COLOR, "Label", LABEL_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(FUNCTION_COLOR, "Function", FUNCTION_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(MARKUP_COLOR, "Markup", MARKUP_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(OPERATOR_COLOR, "Operator", OPERATOR_COLOR + BOLD_SUFFIX, b, p));
		addField(new BooleanColorFieldEditor(DIGIT_COLOR, "Digit", DIGIT_COLOR + BOLD_SUFFIX, b, p));
		addField(new ColorFieldEditor(INVALID_COLOR, "Background", p));
		addField(new ColorFieldEditor(P_CURRENT_LINE_COLOR, "Line Highlight", p));
		addField(new ColorFieldEditor(P_BRACKET_MATCHING_COLOR, "Bracket Match and Whitespace", p));
		addField(new ColorFieldEditor(P_SELECTION_BACKGROUND_COLOR, "Selection Background", p));
	}

	public void init(IWorkbench workbench) {
		if (workbench == null) {
			// ignore
		}
	}
}