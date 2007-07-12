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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rohanclan.afae.partition.AfaePartitionScanner;

/**
 * A rule is like a partition ?
 * 
 * represnts the following element in the mode file:
 * &lt;RULES ESCAPE="\" IGNORE_CASE="FALSE" HIGHLIGHT_DIGITS="TRUE"&gt;
 * 
 * @author robrohan
 */
public class Rule 
{	
	public static final String DEFAULT_NAME = "MAIN";
	
	/** the name for this rule */
	protected String name;
	/** all the types this rule has see Types */
	protected Map<String, List<Type>> types;
	/** all the keywords for this rule */
	protected KeywordMap keywords;
	protected List<Type> orderdList;
	
	protected Mode mode;
	
	protected char escape;
	protected boolean highlightDigits = false;
	protected boolean ignoreCase = true;
	protected String digitRE;
	protected String defaultTokenType = AfaePartitionScanner.NULL;
	
	private static KeywordMap EMPTY_MAP = new KeywordMap(false);
	
	/**
	 * Kind of a singleton type mehtod. Creates and returns one of itsselves
	 * 
	 * @param mode the mode to use
	 * @param name the name for the new rule
	 * @param highlightDigits should the digits be highlighted?
	 * @param ignoreCase case insenstive?
	 * @param digitRE regular expression to find digits
	 * @param escape what is the escape char for strings
	 * @param defaultTokenType default token type (often the NULL type)
	 * @return
	 */
	public static Rule newRule(Mode mode, String name, boolean highlightDigits, 
		boolean ignoreCase, String digitRE, char escape, String defaultTokenType) 
	{		
		Rule newRule = new Rule(name);
		newRule.mode = mode;
		newRule.highlightDigits = highlightDigits;
		newRule.ignoreCase = ignoreCase;
		newRule.digitRE = digitRE;
		newRule.escape = escape;
		newRule.defaultTokenType = defaultTokenType;
		
		return newRule;
	}
	
	/**
	 * Create a new rule by name
	 * @param name
	 */
	public Rule(String name) 
	{
		super();
		types = new HashMap<String, List<Type>>();
		orderdList = new ArrayList<Type>();
		this.name = name;
	}
	
	/**
	 * Gets the rule name
	 * @return
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Add a type to this rule
	 * @param type
	 */
	public void add(Type type)
	{
		orderdList.add(type);
		List<Type> all = types.get(type.getColor());
		
		if(all == null) 
		{
			all = new ArrayList<Type>();
			types.put(type.getColor(), all);
		}
		
		all.add(type);		
	}
	
	/**
	 * Get a list of types from this Rule by Name
	 * TODO: it looks like "type" is by Type.getColor() ?? thats odd
	 * @param type
	 * @return
	 */
	public List get(String type)
	{
		List all = (List)types.get(type);
		
		if(all == null) 
			return Collections.EMPTY_LIST;
		return all;
	}
	
	/**
	 * Human readable string name for this Rule
	 */
	public String toString() 
	{
		return "Rule [" + name + "]";
	}
	
	/**
	 * Get all the keywords that belong to this rule or an EMPTY_MAP (does
	 * not return null)
	 * @return
	 */
	public KeywordMap getKeywords() 
	{
		if(keywords == null) 
			return EMPTY_MAP;
		return keywords;
	}
	
	/**
	 * Get all the types this Rule has
	 * @return
	 */
	public List getTypes() 
	{
		return orderdList;
	}
	
	/**
	 * Set all the keywords this Rule should have
	 * @param keywordMap
	 */
	public void setKeywords(KeywordMap keywordMap) 
	{
		this.keywords = keywordMap;
	}

	/**
	 * Get the default token type
	 * @return
	 */
	public String getDefaultTokenType() 
	{
		return defaultTokenType;
	}

	/**
	 * Get the escape char for stings
	 * @return
	 */
	public char getEscape() 
	{
		return escape;
	}

	/**
	 * does this Rule care about case? true if case does not matter
	 * @return
	 */
	public boolean getIgnoreCase() 
	{
		return ignoreCase;
	}

	/**
	 * Should digit be highlighted - true if yes
	 * @return
	 */
	public boolean getHighlightDigits() 
	{
		return highlightDigits;
	}

	/**
	 * Gets the mode this object is for
	 * @return
	 */
	public Mode getMode() 
	{
		return mode;
	}
}
