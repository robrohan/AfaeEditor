package com.rohanclan.afae.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.prefs.IPreferenceConstants;

public class ToggleWhitespace extends TogglePreferenceAction {
	
	public void run(IAction action) {
		try {
			String id = IPreferenceConstants.P_SHOW_WHITESPACE;
						
			IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
			boolean currentsetting = store.getBoolean(id);
			store.setValue(id, !currentsetting);
			
		} catch (Exception e) {
			AfaePlugin.logError("Couldn't toggle property", e, ToggleWhitespace.class);
		}
	}
	
}
