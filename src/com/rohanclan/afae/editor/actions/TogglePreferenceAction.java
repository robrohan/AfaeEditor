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
package com.rohanclan.afae.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.prefs.IPreferenceConstants;

/**
 * @author Rob
 * 
 * Toggles one of the boolean references based on the id in the plugin .xml file
 */
public class TogglePreferenceAction implements IEditorActionDelegate, IPreferenceConstants {
	protected ITextEditor editor = null;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof ITextEditor) {
			editor = (ITextEditor) targetEditor;
		}
	}

	/**
	 * this gets called for every action
	 */
	public void run(IAction action) {
		try {
			String id = action.getId().toString().replace('_', '.').trim();
						
			IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
			boolean currentsetting = store.getBoolean(id);
			store.setValue(id, !currentsetting);
			
		} catch (Exception e) {
			AfaePlugin.logError("Couldn't toggle property", e, TogglePreferenceAction.class);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {;}
}
