/*
 * 
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
package com.rohanclan.afae.editor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
//import java.util.HashMap;
import java.util.Iterator;
//import java.util.LinkedHashSet;
import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
// import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.modes.EOLSpan;
import com.rohanclan.afae.modes.IVisitor;
import com.rohanclan.afae.modes.Mark;
import com.rohanclan.afae.modes.Mode;
import com.rohanclan.afae.modes.Rule;
import com.rohanclan.afae.modes.Span;
import com.rohanclan.afae.modes.SpanRegexp;
import com.rohanclan.afae.modes.TextSequence;
import com.rohanclan.afae.modes.Type;
//import com.rohanclan.afae.prefs.ColorsPreferencePage;
import com.rohanclan.afae.prefs.IPreferenceConstants;
import com.rohanclan.afae.rules.CasedPatternRule;
import com.rohanclan.afae.rules.ColoringWhitespaceDetector;
import com.rohanclan.afae.rules.ColoringWordDetector;
import com.rohanclan.afae.rules.DelegateToken;
import com.rohanclan.afae.rules.EndOfLineRule;
import com.rohanclan.afae.rules.ITokenFactory;
import com.rohanclan.afae.rules.RegexRule;
import com.rohanclan.afae.rules.StarRule;
import com.rohanclan.afae.rules.TextSequenceRule;

/**
 * Bunch of static helper functions
 * 
 * @author robrohan
 */
public class AfaeEditorTools {
	/** */
	protected static ColoringWordDetector wordDetector = new ColoringWordDetector();

	// the filename to methods tie
	//protected static Map<String, Set<DocumentJumpItem>> documentItemStore = new HashMap<String, Set<DocumentJumpItem>>();

	/**
	 * Changes jedit mode rules to eclipse rules ready to add to scanners
	 * 
	 * @param rule
	 *            the jedit mode rules
	 * @param rules
	 *            a list where the new rules will be added
	 * @param factory
	 *            factory used to get tokens
	 */
	public static void add(Rule rule, List<Object> rules, ITokenFactory factory) {
		List<Type> allTypes = rule.getTypes();

		for (Iterator<Type> typeI = allTypes.iterator(); typeI.hasNext();) {
			Type type = typeI.next();
			add(rule, type, factory, rules);
		}
	}

	/**
	 * Adds the rules for the type to the passed in rule list
	 * 
	 * @param rule
	 * @param type
	 * @param factory
	 * @param rules
	 */
	public static void add(final Rule rule, final Type type, ITokenFactory factory, final List<Object> rules) {
		final IToken token = factory.makeToken(type);
		final Mode mode = rule.getMode();
		final boolean ignoreCase = rule.getIgnoreCase();

		type.accept(new IVisitor() {
			public void acceptSpan(Span span) {
				IToken defaultToken = token;
				
				if (span.hasDelegate()) {
					Rule delegateRule = mode.getRule(span.getDelegate());
					defaultToken = new DelegateToken(type, delegateRule, span.getEnd());
				}

				/*
				 * Using a PatternRule instead of a MultiLineRule because
				 * PatternRule exposes the break on newline behavior.
				 */
				//char span_escape;
				String span_escape;
				if(span.getEscape() != null && !span.getEscape().equals("")){
					span_escape = span.getEscape(); //.charAt(0);
				}else{
					span_escape = mode.getDefaultRuleSet().getEscape() + "";
				}
				
				PatternRule pat = new CasedPatternRule(
					span.getStart(), 
					span.getEnd(), 
					defaultToken, 
					span_escape, 
					span.noLineBreak(), 
					ignoreCase
				);
				((CasedPatternRule)pat).setAtLineStart(span.isAtLineStart());
				((CasedPatternRule)pat).setNoWordBreak(span.isNoWordBreak());
				((CasedPatternRule)pat).setIgnoreCase(span.ignoreCase());
				((CasedPatternRule)pat).setExcludeMatch(span.getExcludeMatch());
				
				rules.add(pat);
			}
			
			public void acceptSpanRegexp(SpanRegexp span) {
				IToken defaultToken = token;
				
				if (span.hasDelegate()) {
					Rule delegateRule = mode.getRule(span.getDelegate());
					defaultToken = new DelegateToken(type, delegateRule, span.getEnd());
				}
				
				//String startSequence, String endSequence, IToken token, String hash_chars
				RegexRule rr = new RegexRule(
					span.getStart(), span.getEnd(), 
					defaultToken, span.getHashChars()
				);
				rr.setAtLineStart(span.isAtLineStart());
				rr.setNoWordBreak(span.isNoWordBreak());
				rr.setIgnoreCase(span.ignoreCase());
				rr.setExcludeMatch(span.getExcludeMatch());
				
				rules.add(rr);
			}
			
			public void acceptTextSequence(TextSequence text) {
				/*
				 * If the text sequence can be recognized as a word, don't add
				 * it. This reduces the number of partitions created. If the
				 * text sequence can not be recognized as a word add it as a
				 * text sequence.
				 */
				if(isWordStart(text.getText().charAt(0)))
					return;
				
				TextSequenceRule tsr = new TextSequenceRule(text.getText(), token, ignoreCase);
				tsr.setAtLineStart(text.isAtLineStart());
				
				rules.add(tsr);
			}

			public void acceptEolSpan(EOLSpan eolSpan) {
				IToken defaultToken = token;
				
				//add the delegate token
				if (eolSpan.hasDelegate()) {
					Rule delegateRule = mode.getRule(eolSpan.getDelegate());
					defaultToken = new DelegateToken(type, delegateRule, eolSpan.getEnd());
				}
				
				EndOfLineRule eolr = new EndOfLineRule(eolSpan.getText(), defaultToken, ignoreCase);
				eolr.setAtLineStart(eolSpan.isAtLineStart());
				eolr.setExcludeMatch(eolSpan.getExcludeMatch());
				eolr.setNoWordBreak(eolSpan.isNoWordBreak());
				
				rules.add(eolr);
			}

			public void acceptMark(Mark mark) {
				// TODO: if I cant get the mark previous rule to act correctly,
				// commenting this out fixes most of the problem
				rules.add(
					new StarRule(mark, new ColoringWhitespaceDetector(), wordDetector, token)
				);
			}
		});
	}

	/**
	 * Check to see if the passed char could be a start of a word character
	 * 
	 * @param c
	 * @return
	 */
	protected static boolean isWordStart(char c) {
		return wordDetector.isWordStart(c);
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property == null)
			return false;

		if (property.endsWith(IPreferenceConstants.BOLD_SUFFIX)) {
			property = property.substring(0, property.length() - IPreferenceConstants.BOLD_SUFFIX.length());
		}
		
		if (property.endsWith(IPreferenceConstants.ITALIC_SUFFIX)) {
			property = property.substring(0, property.length() - IPreferenceConstants.ITALIC_SUFFIX.length());
		}

		boolean affects = property.equals(IPreferenceConstants.COMMENT1_COLOR)
				|| property.equals(IPreferenceConstants.COMMENT2_COLOR)
				|| property.equals(IPreferenceConstants.LITERAL1_COLOR)
				|| property.equals(IPreferenceConstants.LITERAL2_COLOR)
				|| property.equals(IPreferenceConstants.LABEL_COLOR)
				|| property.equals(IPreferenceConstants.KEYWORD1_COLOR)
				|| property.equals(IPreferenceConstants.KEYWORD2_COLOR)
				|| property.equals(IPreferenceConstants.KEYWORD3_COLOR)
				|| property.equals(IPreferenceConstants.FUNCTION_COLOR)
				|| property.equals(IPreferenceConstants.MARKUP_COLOR)
				|| property.equals(IPreferenceConstants.OPERATOR_COLOR)
				|| property.equals(IPreferenceConstants.DIGIT_COLOR)
				|| property.equals(IPreferenceConstants.INVALID_COLOR)
				|| property.equals(IPreferenceConstants.NULL_COLOR);

		AfaePlugin.logDebug("Affects is: " + affects, null, this.getClass());

		return affects;
	}

	/**
	 * Gets the file associated with filename from the plugin direcotry.
	 * Mostly used to get mode files, but can be used to get any type of file from the
	 * plugin directory
	 * @param filename
	 * @return File
	 */
	public static File getFile(String filename) throws IOException {
		if (AfaePlugin.getDefault() != null) {
			URL installURL = AfaePlugin.getDefault().getBundle().getEntry("/");
			URL mode = FileLocator.resolve(new URL(installURL, filename));

			return new File(mode.getFile());
		}
		return new File(filename);
	}

	/**
	 * Gets the white space from the begining of a passed string. Mostly used to
	 * do indentation
	 * 
	 * @param data
	 * @param stopChar
	 * @return
	 */
	public static String getWhiteSpace(String data) {
		String retval = "";
		// boolean found = false;
		int pos;

		for (pos = 0; pos < data.length(); pos++) {
			char currChar = data.charAt(pos);

			if (currChar != ' ' && currChar != '\t') {
				break;
			}
		}
		retval = data.substring(0, pos);

		return retval;
	}

	/**
	 * Gets the whitespace of the previous line. given an offset. If the offset
	 * goes past the begining of the file it returns ""
	 * 
	 * @param doc
	 *            the doc to use
	 * @param offset
	 *            the offset in the file to start from
	 * @throws BadLocationException
	 */
	public synchronized static String getPrevLineWhiteSpace(IDocument doc, int offset) throws BadLocationException {
		int lineNumber = doc.getLineOfOffset(offset);

		if (lineNumber == 0)
			return "";
		int prevLineOffset = doc.getLineOffset(lineNumber);
		String prevLineData = doc.get(prevLineOffset, offset - prevLineOffset);
		return AfaeEditorTools.getWhiteSpace(prevLineData);
	}

	/**
	 * 
	 * @param code
	 * @param indent
	 * @return
	 */
	public static String addIndentation(String code, String indent) {
		// this is a bit of a hack - change whitespace to some high unicode so
		// split doesnt ignore the whitespace
		code = code.replace("\n", "\u2023\u2024");
		code = code.replace(' ', '\u2020');
		code = code.replace('\t', '\u2021');

		String[] lines = code.split("\u2023");

		if (lines.length > 1) {
			StringBuffer indentedcode = new StringBuffer();

			// first line will indent itself
			indentedcode.append(lines[0]);
			indentedcode.append("\n");

			for (int i = 1; i < lines.length; i++) {
				indentedcode.append(indent);
				indentedcode.append(lines[i]);

				if (i != lines.length - 1)
					indentedcode.append("\n");
			}

			return indentedcode.toString().replace('\u2020', ' ').replace('\u2021', '\t').replace("\u2024", "");
		}

		return indent + code.replace('\u2020', ' ').replace('\u2021', '\t').replace("\u2024", "");
	}
}