package com.rohanclan.afae.prefs;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import com.rohanclan.afae.editor.AfaeEditorTools;
import com.rohanclan.afae.prefs.textthemes.TextThemes;

public class ThemePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Combo methodpulldown;
	private Label theme_display;
	private Image itemImage;
	
	//@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		
		Label DimLabelName = new Label(composite, SWT.NONE);
		DimLabelName.setText("Theme: ");
		
		methodpulldown = new Combo(composite, SWT.READ_ONLY);
		methodpulldown.add("--- theme pulldown ---");
		fillCombo(methodpulldown);
		
		methodpulldown.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				String name = methodpulldown.getItem(methodpulldown.getSelectionIndex());
				
				try {
					if(itemImage != null) {
						itemImage.dispose();
					}
					
					File f;
					try {
						f = AfaeEditorTools.getFile(TextThemes.THEME_DIR + name + TextThemes.THEME_PREVIEW_EXT);
					} catch (java.io.FileNotFoundException fnfe) {
						f = AfaeEditorTools.getFile(TextThemes.THEME_DIR + TextThemes.THEME_FALL_BACK + TextThemes.THEME_PREVIEW_EXT);
					}
					
					itemImage = new Image(
						com.rohanclan.afae.AfaePlugin.getDefault().getWorkbench().getDisplay(),
						f.getAbsolutePath()
					);
					theme_display.setBackgroundImage(itemImage);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		Label notelabel = new Label(composite, SWT.NONE);
		notelabel.setText("NOTE: changing themes when you have several files\nopen can take a few seconds.");
		
		theme_display = new Label(composite, SWT.NONE);
		theme_display.setBounds(0, 0, 300, 250);
		//this is a horable hack, but the bounds doesn't seem to do
		//anything :-/
		theme_display.setText("                                                                        \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		
		//return composite;
		return new Composite(parent, SWT.NULL);
	}

	public boolean performOk() {
		String lookupkey = methodpulldown.getItem(methodpulldown.getSelectionIndex());
		
		TextThemes tt = new TextThemes();
		try{
			tt.loadTheme(lookupkey);
			tt.applyTheme();
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	protected void fillCombo(Combo c) {
		c.removeAll();
		
		try{
			File f = AfaeEditorTools.getFile("textthemes");
			if(f.isDirectory()) {
				String[] files = f.list();
				for(int i=0; i < files.length; i++){
					if(files[i].endsWith(".properties"))
						c.add(files[i].replaceAll(".properties", ""));
				}
			}
			
		} catch(IOException e){
			//
		}
	}
	
	public void init(IWorkbench workbench) {;}
	
	
	public void dispose(){
		if(itemImage != null) itemImage.dispose();
		super.dispose();
	}
}
