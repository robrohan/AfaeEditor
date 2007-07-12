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

public interface IModeConstants 
{
	/** the span tag in the mode file */
	public static final String TAG_SPAN = "SPAN";
	/** the eol span tag in the mode file */
	public static final String TAG_EOL_SPAN = "EOL_SPAN";
	/** the regex span tag in the mode file */
	public static final String TAG_SPAN_REGEXP = "SPAN_REGEXP";
	/** the seq tag in the mode file */
	public static final String TAG_SEQ = "SEQ";
	/** the keywords tag in the mode file */
	public static final String TAG_KEYWORDS = "KEYWORDS";
	/** the mark previous tag in the mode file */
	public static final String TAG_MARK_PREVIOUS = "MARK_PREVIOUS";
	/** the mark following tag in the mode file */
	public static final String TAG_MARK_FOLLOWING = "MARK_FOLLOWING";
	/** the whitespace tag in the mode file */
	public static final String TAG_WHITESPACE = "WHITESPACE";
	/** the props wrapper tag for all properties */
	public static final String TAG_PROPS = "PROPS";
	/** a single property tag */
	public static final String TAG_PROPERTY = "PROPERTY";
	/** the rules wrapper tag */
	public static final String TAG_RULES = "RULES";
	
	/** the attribute for the start comment */
	public static final String ATTR_VALUE_START_COMMENT = "commentStart";
	/** the attribute for the end comment */
	public static final String ATTR_VALUE_STOP_COMMENT = "commentEnd";
	
	/** name part of name value on property */
	public static final String ATTR_NAME = "NAME";
	/** value part of name value on property */
	public static final String ATTR_VALUE = "VALUE";
	
	
	/** the default partition type */
	public static final String NULL 		= "NULL";
	/** the comment1 tag in the mode file, as well as the partition key */
	public static final String COMMENT1 	= "COMMENT1";
	/** the comment2 tag in the mode file, as well as the partition key */
	public static final String COMMENT2 	= "COMMENT2";
	/** the literal1 tag in the mode file, as well as the partition key */
	public static final String LITERAL1 	= "LITERAL1";
	/** the literal2 tag in the mode file, as well as the partition key */
	public static final String LITERAL2 	= "LITERAL2";
	/** the label tag in the mode file, as well as the partition key */
	public static final String LABEL 		= "LABEL";
	/** the keyword1 tag in the mode file, as well as the partition key */
	public static final String KEYWORD1 	= "KEYWORD1";
	/** the keyword2 tag in the mode file, as well as the partition key */
	public static final String KEYWORD2 	= "KEYWORD2";
	/** the keyword3 tag in the mode file, as well as the partition key */
	public static final String KEYWORD3 	= "KEYWORD3";
	/** the function tag in the mode file, as well as the partition key */
	public static final String FUNCTION 	= "FUNCTION";
	/** the markup tag in the mode file, as well as the partition key */
	public static final String MARKUP 	= "MARKUP";
	/** the operator tag in the mode file, as well as the partition key */
	public static final String OPERATOR 	= "OPERATOR";
	/** the digit tag in the mode file, as well as the partition key */
	public static final String DIGIT		= "DIGIT";
	/** the invalid tag in the mode file, as well as the partition key */
	public static final String INVALID 	= "INVALID";
}
