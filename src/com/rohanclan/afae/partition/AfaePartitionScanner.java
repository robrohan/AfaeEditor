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
package com.rohanclan.afae.partition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeEditorTools;
import com.rohanclan.afae.editor.ModeCache;
import com.rohanclan.afae.modes.IModeConstants;
import com.rohanclan.afae.modes.Mode;
import com.rohanclan.afae.modes.Rule;
import com.rohanclan.afae.modes.Type;
import com.rohanclan.afae.rules.CToken;
import com.rohanclan.afae.rules.ITokenFactory;

/**
 * 
 * @author robrohan
 */
public class AfaePartitionScanner extends RuleBasedPartitionScanner implements IModeConstants
{	
	protected Mode mode;
	protected int markLength = -1;
	protected char escape;
	protected String filename;

	/**
	 * 
	 * @author robrohan
	 */
	class LitePartitionScanner extends RuleBasedPartitionScanner {
		public int getOffset() {
			return fOffset;
		}
		public void setTokenOffset(int off) {
			fTokenOffset = off;
		}
		public void setOffset(int off) {
			fOffset = off;
		}
	}

	/**
	 * This will setup the rules for elements *not* defined as children of the
	 * keywords elements. JEdit allows the "type" of an element to appear
	 * inside the keywords element or outside. For example you could have the
	 * LITERAL2 tag inside the keywords element and as a top level element.
	 */
	public AfaePartitionScanner(String filename) 
	{
		this(ModeCache.getModeFor(filename));
		this.filename = filename;
	}

	/**
	 * 
	 * @param mode
	 */
	public AfaePartitionScanner(Mode mode) 
	{
		super();
		if(mode == null)
			throw new IllegalArgumentException("Mode cannot be null");
		
		this.mode = mode;
		escape = mode.getDefaultRuleSet().getEscape();
		setPredicateRules(createRuleSet(mode.getDefaultRuleSet()));
	}

	/**
	 * 
	 * @param rule
	 * @return
	 */
	private IPredicateRule[] createRuleSet(Rule rule) 
	{
		List<IRule> rules = new ArrayList<IRule>();
		
		AfaeEditorTools.add(rule, rules, new ITokenFactory() {
			public IToken makeToken(Type type) 
			{
				return new CToken(type);
			}
		});
		
		return (IPredicateRule[])rules.toArray(new IPredicateRule[rules.size()]);
	}

	/**
	 * 
	 */
	public int getTokenLength() 
	{
		if (markLength != -1) {
			int length = markLength;
			markLength = -1;
			return length;
		}
		return super.getTokenLength();
	}

	/**
	 * 
	 * @return
	 */
	public int getOffset()
	{
		return fOffset;
	}

	/**
	 * 
	 * @param loc
	 */
	public void setOffset(int loc) 
	{
		fOffset = loc;
	}
	
	/**
	 * 
	 * @param length
	 */
	public void setMarkLength(int length) 
	{
		markLength = length;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getContentTypes() 
	{
		return mode.getContentTypes();
	}

	/**
	 * 
	 * @return
	 */
	public int backup() 
	{
		--fOffset;
		if(fOffset > -1) 
		{
			try
			{
				return fDocument.getChar(fOffset);
			}
			catch (BadLocationException bl) 
			{
				//ignore
				AfaePlugin.logInfo("backup: this could be normal...",bl,AfaePartitionScanner.class);
			}
		}
		else
		{
			fOffset = 0;
		}
		return ICharacterScanner.EOF;
	}

	/**
	 * Move the token offset by a delta.
	 * 
	 * @param delta - a positive or negative amount to move the token offset.
	 * @nonapi
	 */
	public void moveTokenOffset(int delta) 
	{
		fTokenOffset = fTokenOffset + delta;
	}

	public Mode getMode() 
	{
		return mode;
	}

	/**
	 * @return Returns the filename.
	 */
	public String getFilename() 
	{
		return filename;
	}
}