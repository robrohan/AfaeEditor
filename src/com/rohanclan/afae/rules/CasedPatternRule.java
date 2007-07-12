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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

/**
 * These are the SPAN rules SPAN, EOL_SPAN, SPAN_REGEXP (not implemented),
 * EOL_SPAN_REGEXP (not implemented)
 * 
 * @author robrohan
 */
public class CasedPatternRule extends PatternRule {
	protected boolean ignoreCase;
	protected boolean no_word_break;
	protected String escapeChars;
	protected boolean exclude_match;
	
	//TODO: make this a setting in the mode xml file. For now
	//well just assume NO_WORD_BREAK means space
	protected char[] word_break_chars = new char[]{ ' ' };
		
	/**
	 * 
	 * @param startSequence
	 * @param endSequence
	 * @param token
	 * @param escapeCharacters
	 * @param breaksOnEOL
	 * @param ignoreCase
	 */
	public CasedPatternRule(String startSequence, String endSequence, IToken token, String escapeCharacters, boolean breaksOnEOL, boolean ignoreCase) {
		//we are calling super here mostly for the variable storage. We are going to implement all the
		//important functions ourselves to support multi escape chars (mostly to support php)
		super(
			(ignoreCase ? startSequence.toLowerCase() : startSequence),
			(endSequence == null ? null : (ignoreCase ? endSequence.toLowerCase() : endSequence)), 
			token, 
			escapeCharacters.charAt(0),
			breaksOnEOL
		);
		this.ignoreCase = ignoreCase;
		this.escapeChars = escapeCharacters;
	}
	
	/* Copied from my superclass and modified to support case sensitivity. */
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		if (resume) {
			if (endSequenceDetected(scanner))
				return fToken;
		} else {
			int c = scanner.read();
			
			if(c == ICharacterScanner.EOF) {
				scanner.unread();
				return Token.UNDEFINED;
			}
			
			//if the first characters match, then run it through the full string
			//check to see if they match
			if(charsAreEqual((char)c, fStartSequence[0])){
				if (sequenceDetected(scanner, fStartSequence, false)) {
					//... text read ...
					if(endSequenceDetected(scanner)) {
						/* if(this.exclude_match) {
							for(int q=0; q<this.fEndSequence.length;q++) {
								scanner.unread();
							}
						} */
						return fToken;
					}
				}
			}
		}
		
		scanner.unread();
		return Token.UNDEFINED;
	}

	public void setAtLineStart(boolean to) {
		if(to)
			this.setColumnConstraint(0);
		else
			this.setColumnConstraint(-1);
	}
	
	public void setNoWordBreak(boolean to){
		this.no_word_break = to;
	}
	
	/* Copied from my superclass and modified to support case sensitivity. */
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		int c;
		char[][] delimiters = scanner.getLegalLineDelimiters();
		
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if(isEscapeCharacter(c)) {
				// Skip the escaped character.
				scanner.read();
			} else if (fEndSequence.length > 0 && charsAreEqual((char)c,fEndSequence[0])) {
				// Check if the specified end sequence has been found.
				if(sequenceDetected(scanner, fEndSequence, true))
					return true;
			//if the rule calls for no word break and they type a word break char
			//at present just a space, then break the color coding
			} else if(this.no_word_break && this.isWordBreakChar((char)c)) {
				scanner.unread();
				return false;
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for(int i=0; i < delimiters.length; i++) {
					if (c == delimiters[i][0] && sequenceDetected(scanner, delimiters[i], false))
						return true;
				}
			}
		}

		scanner.unread();
		return true;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	protected boolean charsAreEqual(char a, char b) {
		if(this.ignoreCase) {
			if(Character.toLowerCase(a) == Character.toLowerCase(b))
				return true;
			else
				return false;
		} else {
			if(a == b)
				return true;
			else
				return false;
		}
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	protected boolean isEscapeCharacter(int c) {
		if(this.escapeChars == null) {
			return false;
		}
		
		char[] carray = this.escapeChars.toCharArray();
		int x = carray.length;
		for(int q=0; q<x; q++) {
			if(carray[q] == (char)c)
				return true;
		}
		return false;
	}
	
	/* Copied from my superclass and modified to support case sensitivity. */
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		//This method checks to see which seqence (the rule text) works for the
		//passed data. It stops calling when has a good start maker
		
		boolean abort = false;
		
		for(int i=1; i < sequence.length; i++) {
			int c = scanner.read();
			
			//System.err.println("SD: " + sequence[i] + " " + new String(sequence));
			
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (!this.charsAreEqual((char)c,sequence[i])){
				// Non-matching character detected, rewind the scanner back to
				// the start.
				// Do not unread the first character.
				abort = true;
			}
			
			if(abort){
				scanner.unread();
				for (int j = i - 1; j > 0; j--)
					scanner.unread();
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Check c and returns true if its a word break character
	 * @param c
	 * @return
	 */
	protected boolean isWordBreakChar(char c) {
		//right now just grab the first one, at some point
		//this'll be a loop
		if(c == this.word_break_chars[0]) {
			return true;
		}
		
		return false;
	}

	
	public boolean ignoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public boolean excludeMatch() {
		return exclude_match;
	}

	public void setExcludeMatch(boolean exclude_match) {
		this.exclude_match = exclude_match;
	}
}