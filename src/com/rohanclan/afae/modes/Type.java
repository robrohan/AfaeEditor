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
public abstract class Type {
	protected boolean atLineStart, atWhitespaceEnd, atWordStart;

	protected String text;

	protected String color;

	protected String type;

	/** This represents a single line span. */
	public static final String SINGLE_S = "SINGLE_S";

	/** This represents a multiple line span. */
	public static final String MULTI_S = "MULTI_S";

	/** This represents an end of line span. */
	public static final String EOL_SPAN = "EOL_SPAN";

	/** This represents a sequence of text. */
	public static final String SEQ = "SEQ";

	/** This represents a delegated span. */
	public static final String DELEGATE_S = "DELEGATE_S";

	/**
	 * This represents a sequence of text. It differs from SEQ because the text
	 * that matched may not be highlighted.
	 */
	public static final String MARK_PREVIOUS = "MARK_PREVIOUS";

	/**
	 * This represents a sequence of text. It differs from SEQ because the text
	 * that matched may not be highlighted.
	 */
	public static final String MARK_FOLLOWING = "MARK_FOLLOWING";

	/**
	 * Creates a new End of line span.
	 * 
	 * @param color
	 * @param text
	 * @return
	 */
	public static EOLSpan newEOLSpan(String color, String text, boolean linestart, boolean excludeMatch, 
			boolean noLineBreak, boolean noWordBreak, boolean ignoreCase, 
			String delegate, String escape) {
		
		EOLSpan eol = new EOLSpan();
		eol.type = EOL_SPAN;
		eol.text = text;
		eol.color = color;
		eol.atLineStart = linestart;
		eol.excludeMatch = excludeMatch;
		eol.noLineBreak = noLineBreak;
		eol.noWordBreak = noWordBreak;
		eol.ignoreCase = ignoreCase;
		eol.delegateName = delegate;
		eol.escape = escape;
		
		return eol;
	}

	/**
	 * creates a new span with the given properties
	 * 
	 * @param color
	 * @param begin
	 * @param end
	 * @param atLineStart
	 * @param excludeMatch
	 * @param noLineBreak
	 * @param noWordBreak
	 * @param delegate
	 * @return
	 */
	public static Span newSpan(String color, String begin, String end,
			boolean atLineStart, boolean excludeMatch, boolean noLineBreak,
			boolean noWordBreak, boolean ignoreCase, String delegate, String escape) {

		Span span = new Span();
		span.type = delegate == null ? noLineBreak ? SINGLE_S : MULTI_S	: DELEGATE_S;
		
		span.color = color;
		span.begin = begin;
		span.end = end;
		
		span.excludeMatch = excludeMatch;
		span.atLineStart = atLineStart;
		span.noLineBreak = noLineBreak;
		span.noWordBreak = noWordBreak;
		span.delegateName = delegate;
		span.escape = escape;
		span.setIgnoreCase(ignoreCase);
		return span;
	}

	public static SpanRegexp newSpanRegexp(String color, String begin, String end,
			String hash_chars, boolean atLineStart, boolean excludeMatch, 
			boolean noLineBreak, boolean noWordBreak, boolean ignoreCase, 
			String delegate, String escape) {
		
		//cast it to a SpanRegexp
		SpanRegexp sr = new SpanRegexp();
		sr.type = delegate == null ? noLineBreak ? SINGLE_S : MULTI_S	: DELEGATE_S;
		sr.color = color;
		sr.begin = begin;
		sr.end = end;
		sr.excludeMatch = excludeMatch;
		sr.atLineStart = atLineStart;
		sr.noLineBreak = noLineBreak;
		sr.noWordBreak = noWordBreak;
		sr.delegateName = delegate;
		sr.escape = escape;
		sr.setIgnoreCase(ignoreCase);
		//and set the hash chars since that is the only difference
		sr.setHashChars(hash_chars);
		return sr;
	}
	
	
	/**
	 * Creates a new mark
	 * 
	 * @param color
	 * @param text
	 * @param atLineStart
	 * @param atWhitespaceEnd
	 * @param atWordStart
	 * @param delegate
	 * @param isPrevious
	 * @param excludeMatch
	 * @return
	 */
	public static Span newMark(String color, String text, boolean atLineStart,
			boolean atWhitespaceEnd, boolean atWordStart, String delegate,
			boolean isPrevious, boolean excludeMatch) {

		Mark mark = new Mark();
		mark.type = isPrevious ? MARK_PREVIOUS : MARK_FOLLOWING;
		mark.color = color;
		mark.text = text;
		mark.atLineStart = atLineStart;
		mark.excludeMatch = excludeMatch;
		mark.atWhitespaceEnd = atWhitespaceEnd;
		mark.atWordStart = atWordStart;
		mark.delegateName = delegate;
		mark.isPrevious = isPrevious;
		return mark;
	}

	/**
	 * Creates a new text sequence
	 * 
	 * @param color
	 * @param text
	 * @param atLineStart
	 * @param atWhitespaceEnd
	 * @param atWordStart
	 * @param delegate
	 * @return
	 */
	public static TextSequence newTextSequence(String color, String text,
			boolean atLineStart, boolean atWhitespaceEnd, boolean atWordStart,
			String delegate) {

		TextSequence textSequence = new TextSequence();
		textSequence.type = SEQ;
		textSequence.color = color;
		textSequence.text = text;
		textSequence.atLineStart = atLineStart;
		textSequence.atWhitespaceEnd = atWhitespaceEnd;
		textSequence.atWordStart = atWordStart;
		textSequence.delegateName = delegate;
		return textSequence;
	}

	/**
	 * C'tor
	 */
	public Type() {
		super();
	}

	/**
	 * Get the color for this type
	 * 
	 * @return
	 */
	public String getColor() {
		return color;
	}

	/**
	 * 
	 * @param visitor
	 */
	public abstract void accept(IVisitor visitor);

	/**
	 * Get this text for this type
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * Get the type of this type (see the static finals)
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns true if this type is at the line start
	 * 
	 * @return
	 */
	public boolean isAtLineStart() {
		return atLineStart;
	}

	/**
	 * returns true if this type should stop at whitespace
	 * 
	 * @return
	 */
	public boolean atWhitespaceEnd() {
		return atWhitespaceEnd;
	}

	/**
	 * Gets the content type of this Type
	 * 
	 * @return
	 */
	public String getContentType() {
		return getType() + "." + getColor();
	}

}
