/*
 * Created on Jan 30, 2005
 *
 * The MIT License
 * Copyright (c) 2005 Rob Rohan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package com.rohanclan.afae.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Rob
 *
 * Basic layout for Afae
 */
public class AfaePerspective implements IPerspectiveFactory {
	
	public AfaePerspective() {
		super();
	}
	
	public void createInitialLayout(IPageLayout layout) {
		//this is the main editor - used as a base to place views
		String editorid = layout.getEditorArea();
				
		//view to the left of the editor
		IFolderLayout left = layout.createFolder(
			"left", IPageLayout.LEFT, (float)0.25, editorid
		);
		//standard resource nav (file / project lister)
		left.addView(IPageLayout.ID_RES_NAV);
		
		//views to the bottom
		IFolderLayout bottom = layout.createFolder(
			"bottom", IPageLayout.BOTTOM, (float)0.75, editorid
		);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_BOOKMARKS);
		
		//views to the right
		//IFolderLayout right = layout.createFolder(
		//	"right", IPageLayout.RIGHT, (float)0.75, editorid
		//);
		//right.addView(IPageLayout.ID_OUTLINE);
		
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		
		//views that should show up on the quick menu on show view
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		
		//add wizards here
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
	}
}
