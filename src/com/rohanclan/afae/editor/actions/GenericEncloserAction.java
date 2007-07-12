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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.jface.action.IAction;

import com.rohanclan.afae.AfaePlugin;

/**
 * @author Rob
 *
 * This is the base class for actions that enclose stuff. Like auto adding wrapping
 * comments or quotes etc - it is also used for what other products call snippets 
 */
public class GenericEncloserAction extends Encloser implements IEditorActionDelegate 
{
	protected ITextEditor editor = null;
	protected String start = "";
	protected String end = "";
	
	public GenericEncloserAction()
	{
		super();
	}
	
	public GenericEncloserAction(String start, String end)
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
		if(targetEditor instanceof ITextEditor)
		{
			editor = (ITextEditor)targetEditor;
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
			try
			{
				IDocument doc =  editor.getDocumentProvider().getDocument(editor.getEditorInput());
				ISelection sel = editor.getSelectionProvider().getSelection();
				this.enclose(doc,(ITextSelection)sel,start,end);
				
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

	public void selectionChanged(IAction action, ISelection selection){;}
}
