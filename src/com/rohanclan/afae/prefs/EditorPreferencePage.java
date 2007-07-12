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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
// import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.rohanclan.afae.AfaePlugin;

public class EditorPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage, IPreferenceConstants {

	public EditorPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(AfaePlugin.getDefault().getPreferenceStore());
	}

	protected void createFieldEditors() {
		Composite p = getFieldEditorParent();

		addField(new BooleanFieldEditor(P_SHOW_OVERVIEW_RULER,
				"Show overview &ruler", p));
		addField(new BooleanFieldEditor(P_SHOW_LINE_NUMBERS,
				"Show lin&e numbers", p));
		addField(new BooleanFieldEditor(P_SHOW_WHITESPACE, "Show whitesp&ace",
				p));
		addField(new BooleanFieldEditor(P_HIGHLIGHT_CURRENT_LINE,
				"Hi&ghlight current line", p));
		addField(new BooleanFieldEditor(P_SHOW_PRINT_MARGIN,
				"Sho&w print margin", p));
		addField(new IntegerFieldEditor(P_PRINT_MARGIN_SIZE,
				"&Print margin column:", p));
		addField(new BooleanFieldEditor(P_BRACKET_MATCHING_ENABLED,
				"&Bracket highlighting", p));
		addField(new IntegerFieldEditor(P_TAB_WIDTH, "Displayed &tab width:", p));
		// addField(new BooleanFieldEditor(P_BRACKET_MATCHING_ENABLED, "&Bracket
		// highlighting", p));
		// addField(new BooleanFieldEditor(P_EDITOR_HYPERLINKS_ENABLED, "Live
		// hyperlinks in editor", p));
		// addField(new StringFieldEditor(P_EDITOR_HYPERLINK_KEY_MODIFIER,
		// "Hyperlink key modifier", p));
	}

	public void init(IWorkbench workbench) {
		if (workbench == null) {
			// ignore
		}
	}
}