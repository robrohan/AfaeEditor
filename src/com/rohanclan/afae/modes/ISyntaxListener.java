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
 * 
 * @author robrohan
 */
public interface ISyntaxListener {
	void newRules(String name, boolean highlightDigits, boolean ignoreCase, 
		String digitRE, char escape, String defaultTokenType
	);

	void newEOLSpan(String type, String text, boolean linestart, boolean excludeMatch, 
			boolean noLineBreak, boolean noWordBreak, boolean ignoreCase, 
			String delegate, String escape);

	void newSpan(String type, String begin, String end, boolean atLineStart,
		boolean excludeMatch, boolean noLineBreak, boolean noWordBreak,
		boolean ignoreCase, String delegate, String escape
	);

	void newSpanRegexp(String type, String begin, String end, String hash_chars,
		boolean atLineStart, boolean excludeMatch, boolean noLineBreak, 
		boolean noWordBreak, boolean ignoreCase, String delegate, String escape
	);
	
	void newKeywords(KeywordMap keywords);

	void newTextSequence(String type, String text, boolean atLineStart,
			boolean atWhitespaceEnd, boolean atWordStart, String delegate);

	void newMark(String type, String text, boolean atLineStart,
			boolean atWhitespaceEnd, boolean atWordStart, String delegate,
			boolean isPrevious, boolean excludeMatch);

	/**
	 * Sets the begining of the comment (the wrapper kind) for example /* or
	 * &lt;!--
	 * 
	 * @param begin
	 */
	void setStartComment(String begin);

	/**
	 * Sets the end comment maker for example * / or --&gt;
	 * 
	 * @param end
	 */
	void setEndComment(String end);
}
