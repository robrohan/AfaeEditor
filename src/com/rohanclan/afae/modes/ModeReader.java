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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeEditorTools;
import com.rohanclan.afae.partition.AfaePartitionScanner;

/**
 * Reads a mode file and does call backs to the SyntaxListener to create the
 * needed parts of the mode file
 * 
 * @author robrohan
 */
public class ModeReader implements IModeConstants {
	/** The listener used to build up the rules */
	protected ISyntaxListener listener;

	/**
	 * 
	 * @param listener
	 */
	public ModeReader(ISyntaxListener listener) {
		super();
		this.listener = listener;
	}

	/**
	 * Read in a mode file by filename
	 * 
	 * @param filename
	 */
	@SuppressWarnings("unchecked")
	public void read(String filename) {
		SAXReader reader = new SAXReader();
		Document doc = null;

		try {
			doc = reader.read(AfaeEditorTools.getFile(filename));
		} catch (DocumentException e) {
			AfaePlugin.logError("read() on file " + filename, e, ModeReader.class);
			e.printStackTrace();
			return;
		} catch (IOException ioe) {
			AfaePlugin.logError("read() on file " + filename, ioe, ModeReader.class);
			ioe.printStackTrace();
		}

		Element root = doc.getRootElement();
		
		
		// parse the properties (if they exist)
		if (root.element(TAG_PROPS) != null) {
			for (Iterator<Element> z = root.element(TAG_PROPS).elements(TAG_PROPERTY).iterator(); z.hasNext();) {
				Element singleprop = (Element) z.next();
				String name = singleprop.attributeValue(ATTR_NAME, "");
				String value = singleprop.attributeValue(ATTR_VALUE);

				// if we get the start and end comments set them into the mode
				if (name.equals(ATTR_VALUE_START_COMMENT)) {
					listener.setStartComment(value);
				} else if (name.equals(ATTR_VALUE_STOP_COMMENT)) {
					listener.setEndComment(value);
				}
				
				//Should we add the properties we don't understand?
			}
		}

		// Loop over all the rule in the mode file
		for (Iterator<Element> iter = root.elementIterator(TAG_RULES); iter.hasNext();) {
			Element rulesElement = (Element) iter.next();
			createRule(rulesElement);

			/*
			 * Because order of rules is important we must read the elements in
			 * order instead of using elementIterators("SPAN", etc)
			 */
			List<Element> allTypes = rulesElement.elements();
			for (Iterator<Element> allTypesI = allTypes.iterator(); allTypesI.hasNext();) {
				Element element = (Element) allTypesI.next();
				// does a bit switch...
				createType(element);
			}
		}
	}

	/**
	 * RULEs are handled else where this handles all the items inside a rule
	 * (SPANS, KEYWORDS, etc)
	 * 
	 * @param element
	 *            the DOM element to parse
	 */
	private void createType(Element element) {
		if (element.getName().equals(TAG_SPAN)) {
			createSpan(element);
		} else if (element.getName().equals(TAG_EOL_SPAN)) {
			createEOL(element);
		} else if (element.getName().equals(TAG_SPAN_REGEXP)) {
			createRegexpSpan(element);
		} else if (element.getName().equals(TAG_SEQ)) {
			createTextSequence(element);
		} else if (element.getName().equals(TAG_KEYWORDS)) {
			createKeywords(element);
		} else if (element.getName().equals(TAG_MARK_PREVIOUS)) {
			createMark(element, true);
		} else if (element.getName().equals(TAG_MARK_FOLLOWING)) {
			createMark(element, false);
		} else if (element.getName().equals(TAG_WHITESPACE)) {
			// ignore for now
		} else {
			AfaePlugin.logWarn("Ignore unknown element " + element.getName(),
					null, ModeReader.class);
		}
	}

	/**
	 * Create a keyword Map with AfaePartitionScanner.[CONSTANT] as the key and
	 * an array of strings as the value (see toStringArray)
	 * 
	 * @param keywordsE
	 */
	@SuppressWarnings("unchecked")
	private void createKeywords(Element keywordsE) {
		KeywordMap keywords = new KeywordMap( bool(keywordsE, "IGNORE_CASE", true) );
		keywords.setAtLineStart( bool(keywordsE,"AT_LINE_START",false) );
		
		// ok this is kind of odd... in the keyword map using the
		// PartitionScanner
		// constants add a String[] of keywords for the child elements of
		// <KEYWORD>
		// from teh mode file
		keywords.put(KEYWORD1, toStringArray(keywordsE.elements(KEYWORD1)));
		keywords.put(KEYWORD2, toStringArray(keywordsE.elements(KEYWORD2)));
		keywords.put(KEYWORD3, toStringArray(keywordsE.elements(KEYWORD3)));
		keywords.put(COMMENT1, toStringArray(keywordsE.elements(COMMENT1)));
		keywords.put(COMMENT2, toStringArray(keywordsE.elements(COMMENT2)));
		keywords.put(LITERAL1, toStringArray(keywordsE.elements(LITERAL1)));
		keywords.put(LITERAL2, toStringArray(keywordsE.elements(LITERAL2)));
		keywords.put(LABEL, toStringArray(keywordsE.elements(LABEL)));
		keywords.put(FUNCTION, toStringArray(keywordsE.elements(FUNCTION)));
		keywords.put(MARKUP, toStringArray(keywordsE.elements(MARKUP)));
		keywords.put(OPERATOR, toStringArray(keywordsE.elements(OPERATOR)));
		keywords.put(DIGIT, toStringArray(keywordsE.elements(DIGIT)));
		keywords.put(INVALID, toStringArray(keywordsE.elements(INVALID)));

		listener.newKeywords(keywords);
	}

	/**
	 * Turns a list of keyword elements into a string array for use in the
	 * KeywordMap So, for example:
	 * 
	 * <pre>
	 *  &lt;KEYWORD1&gt;CDATA&lt;/KEYWORD1&gt;
	 *  ...
	 *  &lt;KEYWORD1&gt;EMPTY&lt;/KEYWORD1&gt;
	 * </pre>
	 * 
	 * becomes
	 * 
	 * <pre>
	 *  String[]{ CDATA ,..., EMPTY }
	 * </pre>
	 * 
	 * @param list
	 *            a list of DOM elements of
	 * @return
	 */
	private String[] toStringArray(List<Element> list) {
		if (list.isEmpty())
			return new String[0];
		String[] strings = new String[list.size()];
		int i = 0;

		for (Iterator<Element> iter = list.iterator(); iter.hasNext();) {
			strings[i] = ((Element) iter.next()).getText();
			i++;
		}

		return strings;
	}

	/**
	 * Invokes the listeners newTextSequence to add a new text sequence
	 * 
	 * element from the mode file
	 * 
	 * @param seqElement
	 */
	private void createTextSequence(Element seqElement) {
		boolean atLineStart = bool(seqElement, "AT_LINE_START", false);
		boolean atWhitespaceEnd = bool(seqElement, "AT_WHITESPACE_END", false);
		boolean atWordStart = bool(seqElement, "AT_WORD_START", false);
		String type = seqElement.attributeValue("TYPE");
		String delegate = seqElement.attributeValue("DELEGATE");

		listener.newTextSequence(type, seqElement.getText(), atLineStart,
				atWhitespaceEnd, atWordStart, delegate);
	}

	/**
	 * Invokes the listeners newEOLSpan
	 * 
	 * @param eolElement
	 */
	private void createEOL(Element eolElement) {
		String type = eolElement.attributeValue("TYPE");

		boolean atLineStart = bool(eolElement, "AT_LINE_START", false);
		boolean excludeMatch = bool(eolElement, "EXCLUDE_MATCH", false);
		boolean noLineBreak = bool(eolElement, "NO_LINE_BREAK", false);
		boolean noWordBreak = bool(eolElement, "NO_WORD_BREAK", false);
		boolean ignoreCase = bool(eolElement, "IGNORE_CASE", false);
		String delegate = eolElement.attributeValue("DELEGATE");
		String escape = eolElement.attributeValue("ESCAPE");
		
		//listener.newEOLSpan(type, eolElement.getText(), atLineStart);
		listener.newEOLSpan(type, eolElement.getText(), atLineStart, 
			excludeMatch, noLineBreak, noWordBreak, ignoreCase, 
			delegate, escape);
	}

	/**
	 * Invokes the listeners newSpan
	 * 
	 * @param spanElement
	 */
	private void createSpan(Element spanElement) {
		String type = spanElement.attributeValue("TYPE");

		boolean atLineStart = bool(spanElement, "AT_LINE_START", false);
		boolean excludeMatch = bool(spanElement, "EXCLUDE_MATCH", false);
		boolean noLineBreak = bool(spanElement, "NO_LINE_BREAK", false);
		boolean noWordBreak = bool(spanElement, "NO_WORD_BREAK", false);
		boolean ignoreCase = bool(spanElement, "IGNORE_CASE", false);
		
		String delegate = spanElement.attributeValue("DELEGATE");
		String escape = spanElement.attributeValue("ESCAPE");
		String begin = spanElement.element("BEGIN").getText();
		String end = spanElement.element("END").getText();

		listener.newSpan(type, begin, end, atLineStart, excludeMatch,
			noLineBreak, noWordBreak, ignoreCase, delegate, escape);
	}
	
	private void createRegexpSpan(Element spanElement) {
		String type = spanElement.attributeValue("TYPE");
		
		//HASH_CHAR must be there
		String hash_chars = spanElement.attributeValue("HASH_CHAR");
		boolean atLineStart = bool(spanElement, "AT_LINE_START", false);
		boolean excludeMatch = bool(spanElement, "EXCLUDE_MATCH", false);
		boolean noLineBreak = bool(spanElement, "NO_LINE_BREAK", false);
		boolean noWordBreak = bool(spanElement, "NO_WORD_BREAK", false);
		boolean ignoreCase = bool(spanElement, "IGNORE_CASE", false);

		String delegate = spanElement.attributeValue("DELEGATE");
		String escape = spanElement.attributeValue("ESCAPE");
		String begin = spanElement.element("BEGIN").getText();
		String end = spanElement.element("END").getText();
		
		listener.newSpanRegexp(
			type, begin, end, hash_chars, atLineStart, excludeMatch,
			noLineBreak, noWordBreak, ignoreCase, delegate, escape
		);
	}

	/**
	 * Invokes the listeners newMark
	 * 
	 * @param markElement
	 * @param isPrevious
	 */
	private void createMark(Element markElement, boolean isPrevious) {
		boolean atLineStart = bool(markElement, "AT_LINE_START", false);
		boolean atWhitespaceEnd = bool(markElement, "AT_WHITESPACE_END", false);
		boolean excludeMatch = bool(markElement, "EXCLUDE_MATCH", false);
		boolean atWordStart = bool(markElement, "AT_WORD_START", false);
		String type = markElement.attributeValue("TYPE");
		String delegate = markElement.attributeValue("DELEGATE");

		listener.newMark(type, markElement.getText(), atLineStart,
				atWhitespaceEnd, atWordStart, delegate, isPrevious,
				excludeMatch);
	}

	/**
	 * Invokes the listeners newRules Create a rule from an XML element in the
	 * mode file (DOM)
	 * 
	 * @param rulesElement
	 */
	protected void createRule(Element rulesElement) {
		// get the name for this set of rules, and use MAIN if its the main set
		// of
		// rules because it may not have an attribtues "SET"
		String name = rulesElement.attributeValue("SET", Rule.DEFAULT_NAME);

		// read in the rules configuration and setup defaults if they dont exist
		boolean highlightDigits = bool(rulesElement, "HIGHLIGHT_DIGITS", false);
		boolean ignoreCase = bool(rulesElement, "IGNORE_CASE", true);
		String digitRE = rulesElement.attributeValue("DIGIT_RE");
		char escape = rulesElement.attributeValue("ESCAPE", "" + (char) 0)
				.charAt(0);
		String defaultTokenType = rulesElement.attributeValue("DEFAULT",
				AfaePartitionScanner.NULL);

		// create a new rule
		listener.newRules(name, highlightDigits, ignoreCase, digitRE, escape,
				defaultTokenType);
	}

	/**
	 * Create a java boolean out of a ture/false setting in the mode file
	 * 
	 * @param element
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	protected boolean bool(Element element, String attributeName,
			boolean defaultValue) {
		return Boolean.valueOf(
				element.attributeValue(attributeName, String
						.valueOf(defaultValue))).booleanValue();
	}
}
