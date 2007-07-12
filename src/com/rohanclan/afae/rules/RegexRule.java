package com.rohanclan.afae.rules;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.rohanclan.afae.AfaePlugin;

public class RegexRule extends SingleLineRule {
	protected char[] hash_chars = null;
	
	protected RE re = null;
	
	public RegexRule(String startSequence, String endSequence, IToken token, String hash_chars) {
		super(startSequence, endSequence, token, (char)0, false);
		this.setHashChars(hash_chars);
		fBreaksOnEOL = false;
		
		try {
			re = new RE(startSequence);
			//this.fStartSequence = this.hash_chars;
		} catch (RESyntaxException e) {
			AfaePlugin.logError("Bunk Regex Rule: " + startSequence, e, RegexRule.class);
			e.printStackTrace();
		}
	}
	
	/*
	 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
	 * @since 2.0
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (fColumn == UNDEFINED)
			return doEvaluate(scanner, resume);

		int c= scanner.read();
		scanner.unread();
		if (this.isValidHashChar((char)c))
			return (fColumn == scanner.getColumn() ? doEvaluate(scanner, resume) : Token.UNDEFINED);
		return Token.UNDEFINED;
	}
	
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		//if the RE couldn't get created for some reason drop out right away
		if(re == null) {
			//scanner.unread();
			return Token.UNDEFINED;
		}
		
		//I think this means, if we got sucked away into another partition
		//but now we are back. Check to see if it's closed now...
		if (resume) {
			if (endSequenceDetected(scanner))
				return fToken;
		} else {
			//first time in, check to see if it looks like we need to check
			//this regex
			int c = scanner.read();
			//scanner.unread();
			
			if(c == ICharacterScanner.EOF) {
				scanner.unread();
				return Token.UNDEFINED;
			}
			
			//if the first characters match, then run it through the full string
			//check to see if they match
			//if(charsAreEqual((char)c, fStartSequence[0])){
			if(this.isValidHashChar((char)c)) {
				
				//if it is a valid char unread the scanner so the regex can have the
				//full string to match against
				scanner.unread();
				
				//if (sequenceDetected(scanner, fStartSequence, false)) {
				if (sequenceDetected(scanner, null, false)) {
					//... typing here ...
					
					if(endSequenceDetected(scanner)) {
						//in the regex rule, exclude match means don't include
						//the end anchor
						if(this.exclude_match) {
							for(int q=0; q<this.fEndSequence.length; q++) {
								scanner.unread();
							}
						}
						return fToken;
					} //done end sequence check
				
				} //done sequence check
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
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
				if (super.sequenceDetected(scanner, fEndSequence, true))
					return true;
			//if the rule calls for no word break and they type a word break char
			//at present just a space, then break the color coding
			} else if(this.no_word_break && this.isWordBreakChar((char)c)) {
				scanner.unread();
				return false;
			//if END is blank, assume the regex ends at end of line
			} else if (fBreaksOnEOL || this.fEndSequence.length == 0) {
				// Check for end of line since it can be used to terminate the pattern.
				for(int i=0; i < delimiters.length; i++) {
					if(c == delimiters[i][0] && super.sequenceDetected(scanner, delimiters[i], false))
						return true;
				}
			}
			
		}

		scanner.unread();
		return true;
	}
	
	protected boolean isEndOfLine(char c, ICharacterScanner scanner) {
		if((int)c == -1) return true;
		
		//we need to die on EOL too
		char[][] delimiters = scanner.getLegalLineDelimiters();
		
		for(int i=0; i < delimiters.length; i++) {
			if(c == delimiters[i][0] && super.sequenceDetected(scanner, delimiters[i], false)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		StringBuffer buf = new StringBuffer();
		boolean first_pass = false;
		boolean done_passing = false;
		
		int c;
		int read_count = 0;
		//Ok, this is a bit odd. What we are going to do is keep building up
		//the chars after the initial trigger hash char. We'll pile them up 
		//and run them though the regex on each iteration. Then we'll keep 
		//going until the regex fails, that way we wont miss anything for the
		//regex. For example [a-z]{1,} should keep going until the rule fails
		//but it will trigger a hit right after one letter.
		
		//ICharacterScanner.EOF
		while( !isEndOfLine( (char)(c = scanner.read()), scanner ) ) {
		//while( ((c = scanner.read()) != ICharacterScanner.EOF) ) {
			read_count++;
			buf.append((char)c);
			
			if((int)c == -1) break;
			
			if(!first_pass){
				if(re.match(buf.toString())) {
					first_pass = true;
					break;
				}
			} /* else {
				if(!re.match(buf.toString())){
					done_passing = true;
					break;
				}
			} */
		}
		
		//we got a hit and then had some extra information, return true about
		//the hit, and drop the char that killed the regex
		if(first_pass && done_passing) {
			scanner.unread();
			return true;
		} else if(first_pass) {
			//we got a hit and it was everything there was
			return true;
		} else {
			//we hit EOL and didn't match at all, unread out gobbler
			//scanner.unread();
			for(int j = (read_count-1); j > 0; j--)
				scanner.unread();
			
			return false;
		}
	}
	
	protected boolean isValidHashChar(char c) {
		for(int i=0; i < this.hash_chars.length; i++) {
			if(c == this.hash_chars[i])
				return true;
		}
		return false;
	}

	public void setHashChars(String chars) {
		hash_chars = chars.toCharArray();
	}
}
