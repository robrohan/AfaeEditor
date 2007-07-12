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
 * &lt;SPAN TYPE="LITERAL1" NO_LINE_BREAK="TRUE"&gt;
 * &lt;BEGIN&gt;"&lt;/BEGIN&gt; &lt;END&gt;"&lt;/END&gt; &lt;/SPAN&gt;
 * 
 * @author robrohan
 */
public class Span extends TextSequence {
	protected String begin, end;
	protected String escape = "";

	protected boolean noLineBreak, noWordBreak, excludeMatch, ignoreCase;

	/**
	 * 
	 */
	public void accept(IVisitor visitor) {
		visitor.acceptSpan(this);
	}

	/**
	 * Get the starting sequence of this span
	 * 
	 * @return
	 */
	public String getStart() {
		return begin;
	}

	/**
	 * Get the end sequence of this span
	 * 
	 * @return
	 */
	public String getEnd() {
		return end;
	}
	
	/**
	 * Returns the escape char for this span or
	 * empty string if it doesn't exist
	 * @return
	 */
	public String getEscape() {
		return escape;
	}

	/**
	 * Does this have a delegate? (calles another set of rules) for example:
	 * &lt;SPAN TYPE="KEYWORD2" DELEGATE="ENTITY-TAGS"&gt; where later there
	 * would be &lt;RULES SET="ENTITY-TAGS" DEFAULT="KEYWORD2"&gt;
	 * 
	 * @return true if there is a delegate
	 */
	public boolean hasDelegate() {
		return getDelegate() != null;
	}

	/**
	 * Returns true if this span does not allow line breaks
	 * 
	 * @return
	 */
	public boolean noLineBreak() {
		return noLineBreak;
	}

	/**
	 * Returns true if this span does not allow work breaks
	 * 
	 * @return
	 */
	public boolean isNoWordBreak() {
		return noWordBreak;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getExcludeMatch() {
		return excludeMatch;
	}

	/**
	 * Get the content type this should delegate to. for example:
	 * 
	 * &lt;SPAN TYPE="MARKUP" DELEGATE="javascript::MAIN"&gt;
	 * 
	 * @return
	 */
	public String getDelegateContentType() {
		return getDelegate() + getContentType();
	}

	public boolean ignoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
}
