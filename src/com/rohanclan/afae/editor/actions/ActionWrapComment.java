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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeEditor;
import com.rohanclan.afae.editor.AfaeSourceViewerConfiguration;
import com.rohanclan.afae.modes.Mode;

/**
 * @author Rob
 *
 * This adds comments around the selected text (or just sticks
 * in the comments if no text is selected) 
 */
public class ActionWrapComment extends Encloser implements IEditorActionDelegate
{
	protected AfaeEditor editor = null;
	protected String start = "";
	protected String end = "";
	
	public ActionWrapComment()
	{
		super();
	}
		
	public ActionWrapComment(String start, String end)
	{
		super();
		setEnclosingStrings(start,end);
	}
	
	public void setEnclosingStrings(String start, String end)
	{
		this.start = start;
		this.end = end;	
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) 
	{
		if(targetEditor instanceof AfaeEditor)
		{
			editor = (AfaeEditor)targetEditor;
		}
	}
	
	public void run()
	{
		run(null);
	}
		
	public void run(IAction action) 
	{
		if(editor != null)
		{
			IDocument doc =  editor.getDocumentProvider().getDocument(editor.getEditorInput());
			ISelection sel = editor.getSelectionProvider().getSelection();
			
			AfaeSourceViewerConfiguration asvc = editor.getSourceViewerConfig();
			
			if(asvc != null)
			{
				Mode editormode = asvc.getMode();
				String begin = editormode.getBeginComment();
				String cend = editormode.getStopComment();
								
				if(begin.length() > 0 && cend.length() > 0)
				{
					setEnclosingStrings(begin+" "," "+cend);
					try
					{
						enclose(doc,(ITextSelection)sel,start,end);
						
						//move the cursor to before the end of the new insert
						int offset = ((ITextSelection)sel).getOffset();
						offset += ((ITextSelection)sel).getLength();
						offset += start.length();
						editor.setHighlightRange(offset,0,true);
					}
					catch(Exception e)
					{
						AfaePlugin.logError("run",e,ActionWrapComment.class);
					}
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection){;}
}