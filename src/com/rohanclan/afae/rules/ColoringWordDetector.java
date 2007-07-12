/*
 * cbg.editor Eclipse plugin
 * Copyright(c) 2002, Chris Grindstaff
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
package com.rohanclan.afae.rules;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.rules.IWordDetector;

public class ColoringWordDetector implements IWordDetector {
	protected Set<Integer> charMap = new HashSet<Integer>();
	
    public boolean isWordStart(char c) 
    {
    		return Character.isLetter(c) || '_' == c || isTextSequencePart(c);
    }

	private boolean isTextSequencePart(char c) 
	{
		return charMap.contains(new Integer(c));
	}

	public void addWord(String word) 
	{
		charMap.add(new Integer(word.charAt(0)));
	}

    public boolean isWordPart(char c) 
    {
    		/* added the dot so properties file would mark_following correctly
    		 * when they path contained dots. For example a.b.c=124
    		 * added the - for css and xslt methods
    		 */
		//return isWordStart(c) || Character.isDigit(c) || '.' == c || '-' == c;
    		return isWordStart(c) || Character.isDigit(c) || '-' == c;
    }
}
