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

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.BadLocationException;

import com.rohanclan.afae.editor.AfaeEditorTools;

/**
 * @author Rob
 *
 * This is mostly used as a class to wrap text with something (like a comment,
 * or a cf variable, etc). This class is often extended not used directly
 */
public class Encloser {
	/** 
	 * Wraps the selection with the start and end string
	 * @param doc the document this belongs to
	 * @param sel the selection to be wrapped
	 * @param start the string to put before the selection
	 * @param end the string to put before the selection
	 */
	public void enclose(IDocument doc, ITextSelection sel, String start, String end)
	{
		try
		{
			if(!sel.isEmpty())
			{
				StringBuffer cmtpart = new StringBuffer();
			
				int offset = sel.getOffset();
				int len  = sel.getLength();
				
				cmtpart.append(start);
				//if it looks like this is an insert that needs to be indented try
				//to add it to the lines
				if(start.indexOf("\n") >= 0 || end.indexOf("\n") >= 0)
				{
					String thecode = doc.get(offset,len);
					
					thecode = AfaeEditorTools.addIndentation(
						thecode,
						AfaeEditorTools.getPrevLineWhiteSpace(doc,offset)
					);
					
					cmtpart.append(thecode);
				}
				else
				{
					cmtpart.append(doc.get(offset,len));	
				}
				cmtpart.append(end);
				
				doc.replace(offset, len, cmtpart.toString());
			}
		}
		catch(BadLocationException ble)
		{
			ble.printStackTrace(System.err);
		}
	}
}
