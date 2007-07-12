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
package com.rohanclan.afae.modes;

/**
 * Represents an element like this in the mode file:
 * 
 * &lt;MARK_PREVIOUS TYPE="FUNCTION" EXCLUDE_MATCH="TRUE"&gt;(&lt;/MARK_PREVIOUS&gt;
 * 
 * @author robrohan
 */
public class Mark extends Span 
{
	protected boolean isPrevious;
	
	/**
	 * true if mark previous is set
	 * (element mark_previous)
	 * @return
	 */
	public boolean isMarkPrevious() 
	{
		return isPrevious;
	}
	
	/**
	 * true if mark following is set
	 * (element mark_following)
	 * @return
	 */
	public boolean isMarkFollowing() 
	{
		return !isPrevious;
	}
	
	/**
	 * 
	 */
	public void accept(IVisitor visitor) 
	{
		visitor.acceptMark(this);
	}

	/**
	 * Gets the type of this mark
	 */
	public String getType() 
	{
		return super.getType() + "@" + text.length();
	}

}
