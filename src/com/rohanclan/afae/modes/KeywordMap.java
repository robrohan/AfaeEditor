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

import java.util.HashMap;
import java.util.Map;

/**
 * A Map of keywords 
 * @author robrohan
 */
public class KeywordMap 
{
	/** the keywords */
	protected Map<Object,Object> keywords;
	/** does case matter for these keywords */
	protected boolean ignoreCase;
	protected boolean at_line_start = false;
	
	/**
	 * C'tore
	 * @param ignoreCase true if case doesnt matter
	 */
	public KeywordMap(boolean ignoreCase) 
	{
		super();
		keywords = new HashMap<Object,Object>();
		this.ignoreCase = ignoreCase;
	}
	
	/**
	 * Add a keyword to this map
	 * @param key one of IModeConstants.[CONSTANT] (non TAG_ type)
	 * @param value a string array of keywords
	 */
	public void put(Object key, Object value) 
	{
		keywords.put(key, value);
	}
	
	/**
	 * returns true if keyword case doesnt matter
	 * @return
	 */
	public boolean ignoreCase() 
	{
		return ignoreCase;
	}
	
	/**
	 * Gets a string array of keywords
	 * @param key one of AfaePartitionScanner.[CONSTANT]
	 * @return a string array of keywords
	 */
	public String[] get(Object key) 
	{
		return (String[]) keywords.get(key);
	}

	public boolean isAtLineStart() {
		return at_line_start;
	}

	public void setAtLineStart(boolean at_line_start) {
		this.at_line_start = at_line_start;
	}
}
