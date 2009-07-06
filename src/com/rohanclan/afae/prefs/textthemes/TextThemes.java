package com.rohanclan.afae.prefs.textthemes;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeEditorTools;
import com.rohanclan.afae.prefs.IPreferenceConstants;

public class TextThemes implements IPreferenceConstants {
	/** the directory where the themes are: textthemes/ */
	public static final String THEME_DIR = "textthemes/";
	public static final String THEME_EXT = ".properties";
	public static final String THEME_PREVIEW_EXT = ".jpg";
	public static final String THEME_FALL_BACK = "Afae";
	
	protected Properties p;
	
	public void loadTheme(String name) throws IOException {
		p = new Properties();
		p.load(new FileInputStream(AfaeEditorTools.getFile(THEME_DIR + name + THEME_EXT)));
	}
	
	public void applyTheme() {
		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
		
		@SuppressWarnings("unchecked")
		Enumeration e = p.keys();
		
		while(e.hasMoreElements()){
			String key = (String)e.nextElement();
			try{
				//if the key contains "color" and not "bold" assume it's a color
				//otherwise assume the value is boolean.
				
				//set the new style text decoration rule
				if( key.toLowerCase().indexOf("color") > 0 && key.indexOf(TEXT_PROPERTY_SUFFIX) > 0 ) 
				{
					//System.err.println("key_1: " + key + " value: " + p.getProperty(key));
					
					String prop = p.getProperty(key);
					String[] textattrs = prop.split(",");
					//should be boolean, boolean, boolean, boolean if not
					//dump out
					if(textattrs.length != 4) {
						continue;
					}
					
					//get the main key color so we can set the values properly
					String rule_base = key.substring(0, (key.length() - TEXT_PROPERTY_SUFFIX.length()) );
					//B I U S
					store.setValue(rule_base + BOLD_SUFFIX, Boolean.parseBoolean(textattrs[0].trim()) );
					store.setValue(rule_base + ITALIC_SUFFIX, Boolean.parseBoolean(textattrs[1].trim()) );
					store.setValue(rule_base + UNDERLINE_SUFFIX, Boolean.parseBoolean(textattrs[2].trim()) );
					store.setValue(rule_base + STRIKE_SUFFIX, Boolean.parseBoolean(textattrs[3].trim()) );
				}
				//is this a text attribute background color setting
				else if( key.toLowerCase().indexOf("color") > 0 && key.indexOf(BACKGROUND_COLOR_SUFFIX) > 0 ) {
					//System.err.println("This is a background color property: " + key + " value: " + p.getProperty(key));
					store.setValue(key, p.getProperty(key));
				}
				//not a text attribute setting, perhaps just a color setting
				else if( key.toLowerCase().indexOf("color") > 0 && 
					(key.indexOf(BOLD_SUFFIX) < 0 && key.indexOf(ITALIC_SUFFIX) < 0
					 && key.indexOf(TEXT_PROPERTY_SUFFIX) < 0 && key.indexOf(BACKGROUND_COLOR_SUFFIX) < 0) 
				) {
					//System.err.println("This is a Color setting (not background): " + key + " value: " + p.getProperty(key));
					store.setValue(key, p.getProperty(key));
				//it's none of those, assume it's just a boolean setting
				} else {
					//System.err.println("This is a boolean setting: " + key + " value: " + p.getProperty(key));
					store.setValue(key, Boolean.valueOf(p.getProperty(key)));	
				}	
			} catch (Exception ex) {
				//We kind of don't care if this errors, because they could have odd prefereces
				//set in the file. i.e. newly supported items, or non supported items so we'll
				//just be quite if we error
			}
		}
	}
}
