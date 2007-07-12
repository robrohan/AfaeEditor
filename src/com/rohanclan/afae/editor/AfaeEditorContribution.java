/*
 * Copyright (c) 2002, 2005 Chris Grindstaff, Rob Rohan 
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
package com.rohanclan.afae.editor;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

import com.rohanclan.afae.AfaePlugin;

/**
 * Adds the mode to the status bar when a type is loaded
 * @author robrohan
 */
public class AfaeEditorContribution extends BasicTextEditorActionContributor 
{
	protected StatusLineContributionItem modeStatus;
	
	public AfaeEditorContribution() 
	{
		super();
		modeStatus = new StatusLineContributionItem(AfaePlugin.STATUS_CATEGORY_MODE);
	}

	/*
	 * 
	 */
	public void contributeToStatusLine(IStatusLineManager statusLineManager) 
	{
		super.contributeToStatusLine(statusLineManager);
		statusLineManager.add(modeStatus);
	}

	/*
	 * 
	 */
	public void setActiveEditor(IEditorPart targetEditor) 
	{
		super.setActiveEditor(targetEditor);
        if (targetEditor instanceof ITextEditorExtension) 
        {
            ((ITextEditorExtension)targetEditor).setStatusField(modeStatus, AfaePlugin.STATUS_CATEGORY_MODE);
        }
        
        if(targetEditor instanceof AfaeEditor)
            updateTitle((AfaeEditor)targetEditor);
	}

	/**
	 * Set the mode text on the status bar
	 * @param editor
	 */
	private void updateTitle(AfaeEditor editor) 
	{
		IEditorInput input = editor.getEditorInput();
		if(input == null) 
			return;
		
		IStatusLineManager statusLine = getActionBars().getStatusLineManager();
		
		if(statusLine == null) 
			return;
		
		statusLine.setMessage(input.getToolTipText());
	}
}