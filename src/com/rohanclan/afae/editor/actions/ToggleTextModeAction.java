package com.rohanclan.afae.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.prefs.IPreferenceConstants;

public class ToggleTextModeAction implements IEditorActionDelegate,	IPreferenceConstants {
	protected ITextEditor editor = null;
		
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof ITextEditor) {
			editor = (ITextEditor) targetEditor;
		}
	}

	public void run(IAction action) {
		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
		boolean textmode_on = store.getBoolean(P_USE_WORD_WRAP);
		
		//if wordwrap is on, text mode is on
		if(textmode_on) {
			//code mode
			this.togglePreference(P_USE_WORD_WRAP, false);
			this.togglePreference(P_USE_WIDE_CARET, false);
			this.togglePreference(P_HIGHLIGHT_CURRENT_LINE, true);
			this.togglePreference(P_SHOW_LINE_NUMBERS, true);
		} else {
			//text mode
			this.togglePreference(P_USE_WORD_WRAP, true);
			this.togglePreference(P_USE_WIDE_CARET, true);
			this.togglePreference(P_HIGHLIGHT_CURRENT_LINE, false);
			this.togglePreference(P_SHOW_LINE_NUMBERS, false);
		}
	}

	private void togglePreference(String id, boolean setting) {
		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
		store.setValue(id, setting); 
	}
	
	public void selectionChanged(IAction action, ISelection selection) {;}
}
