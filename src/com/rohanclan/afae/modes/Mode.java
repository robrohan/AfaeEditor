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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.eclipse.jface.text.IDocument;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.ModeCache;

/**
 * Represents one instance of a mode file
 * 
 * @author robrohan
 */
public class Mode {
	
	class ModeSyntaxListener implements ISyntaxListener {
		
		public void newEOLSpan(String type, String text, boolean linestart, boolean excludeMatch, 
				boolean noLineBreak, boolean noWordBreak, boolean ignoreCase, 
				String delegate, String escape) {
			
			EOLSpan eol = Type.newEOLSpan(type, text, linestart, excludeMatch, 
				noLineBreak, noWordBreak, ignoreCase, delegate, escape);
			
			currentRule.add(eol);
		}

		public void newKeywords(KeywordMap keywords) {
			currentRule.setKeywords(keywords);
		}

		public void newRules(String theName, boolean highlightDigits,
			boolean ignoreCase, String digitRE, char escape, String defaultTokenType) {
			
			currentRule = Rule.newRule(Mode.this, theName, highlightDigits,
				ignoreCase, digitRE, escape, defaultTokenType
			);

			Mode.this.add(currentRule);
		}

		public void newSpan(String type, String begin, String end,
			boolean atLineStart, boolean excludeMatch, boolean noLineBreak,
			boolean noWordBreak, boolean ignoreCase, String delegate, String escape) {
			
			currentRule.add(
				Type.newSpan(type, begin, end, atLineStart, excludeMatch, noLineBreak, 
					noWordBreak, ignoreCase, delegate, escape
				)
			);
		}

		public void newSpanRegexp(String type, String begin, String end, String hash_chars, 
				boolean atLineStart, boolean excludeMatch, boolean noLineBreak, 
				boolean noWordBreak, boolean ignoreCase, String delegate, String escape) {
			
			currentRule.add(
				Type.newSpanRegexp(type, begin, end, hash_chars, atLineStart, 
					excludeMatch, noLineBreak, noWordBreak, ignoreCase, delegate, escape
				)
			);
		}
		
		public void newMark(String type, String text, boolean atLineStart,
			boolean atWhitespaceEnd, boolean atWordStart, String delegate,
			boolean isPrevious, boolean excludeMatch) {
			
			currentRule.add(
				Type.newMark(type, text, atLineStart,
					atWhitespaceEnd, atWordStart, delegate, isPrevious,	excludeMatch
				)
			);
		}

		public void newTextSequence(String type, String text,
			boolean atLineStart, boolean atWhitespaceEnd,
			boolean atWordStart, String delegate) {
			
			currentRule.add(
				Type.newTextSequence(
					type, text, atLineStart, atWhitespaceEnd, atWordStart, delegate
				)
			);
		}

		public void setStartComment(String begin) {
			setBeginComment(begin);
		}

		public void setEndComment(String end) {
			setStopComment(end);
		}

		public void addProperty(String name, String value) {
			addProperty(name, value);
		}
	}

	protected Map<String, String> properties;

	/** the rules for this Mode */
	protected Map<String, Rule> rules;

	protected Map<String, Rule> delegates;

	protected String name;

	protected String displayName;

	protected String filename;

	protected String filenameGlob;

	protected String firstLineGlob;

	/** the supported content types */
	protected String[] contentTypes;

	protected RE re;

	protected boolean isLoaded;

	protected Rule currentRule;

	protected String beginComment = "";

	protected String endComment = "";

	/**
	 * 
	 * @param name
	 */
	public Mode(String name) {
		super();
		properties = new HashMap<String, String>();
		this.name = name.toLowerCase();
		rules = new HashMap<String, Rule>();
		delegates = new HashMap<String, Rule>();
		displayName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * Gets all the rules this Mode has (see the Rule class
	 * 
	 * @return
	 */
	public Collection<Rule> getRules() {
		return rules.values();
	}

	/**
	 * Add a rule to the list of rules for this mode
	 * 
	 * @param rule
	 */
	public void add(Rule rule) {
		rules.put(rule.getName(), rule);
	}

	/**
	 * Set the default rule
	 * 
	 * @return
	 */
	public Rule getDefaultRuleSet() {
		return (Rule) rules.get(Rule.DEFAULT_NAME);
	}

	/**
	 * Answer the named rule. This may be a rule local to this mode or may refer
	 * to a rule in another mode.
	 * 
	 * BUGFIX I also found that some of the modes specify modes that do not
	 * exist. (asp.xml) in cases where that happens answer the default rule.
	 * 
	 * @param name
	 * @return Rule
	 */
	public Rule getRule(String theName) {
		int colonIndex = theName.indexOf("::");
		Rule localRule;
		if (colonIndex == -1) {
			localRule = (Rule) rules.get(theName);
			if (localRule == null)
				localRule = getDefaultRuleSet();
			return localRule;
		}
		return ModeCache.resolveDelegate(this, theName);
	}

	/*
	 * 
	 */
	public String toString() {
		return "Mode [" + name + "]";
	}

	/**
	 * 
	 * @param name
	 * @param filename
	 * @param fileGlob
	 * @param firstLineGlob
	 * @return
	 */
	public static Mode newMode(String name, String filename, String fileGlob,
			String firstLineGlob) {
		Mode mode = new Mode(name);
		mode.filename = filename;
		mode.filenameGlob = fileGlob;
		mode.firstLineGlob = firstLineGlob;
		return mode;
	}

	/**
	 * 
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Answer true if the receiver can represent the filename.
	 */
	public boolean matches(String aFilename) {
		if (re == null)
			createRE();
		return re.match(aFilename);
	}

	public Map<String, Rule> getDelegates() {
		return delegates;
	}

	private void createRE() {
		try {
			if (filenameGlob == null) {
				re = new RE(filename);
			}

			if (filenameGlob == null) {
				re = new RE(filename + "$");
				return;
			}

			StringBuffer buf = new StringBuffer();

			for (int i = 0; i < filenameGlob.length(); i++) {
				char c = filenameGlob.charAt(i);
				switch (c) {
				case '*':
					buf.append(".*");
					break;
				case '{':
					buf.append('(');
					break;
				case ',':
					buf.append('|');
					break;
				case '}':
					buf.append(')');
					break;
				case '[':
					buf.append('[');
					break;
				case ']':
					buf.append(']');
					break;
				case '.':
				case '\\':
				case '+':
				case '?':
				case '$':
				case '^':
				case '|':
				case '(':
				case ')':
					buf.append('\\');
				default:
					buf.append(c);
					break;
				}
			}
			buf.append('$');
			re = new RE(buf.toString());
		} catch (RESyntaxException e) {
			AfaePlugin.logError("createRE Regex Error", e, Mode.class);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * 
	 */
	public void load() {
		isLoaded = true;
		ModeReader reader = new ModeReader(new ModeSyntaxListener());
		reader.read("modes/" + filename);

		AfaePlugin.logDebug("Parsed Syntax File " + filename, null, this
				.getClass());
	}

	/**
	 * 
	 * @return
	 */
	public boolean notLoaded() {
		return !isLoaded;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getContentTypes() {
		if (contentTypes == null) {
			Set<String> contentTypesList = new HashSet<String>(15);
			contentTypesList.add(IDocument.DEFAULT_CONTENT_TYPE);
			Rule rule = getDefaultRuleSet();
			List<Type> allTypes = rule.getTypes();
			for (Iterator<Type> typeI = allTypes.iterator(); typeI.hasNext();) {
				Type type = (Type) typeI.next();
				contentTypesList.add(type.getContentType());
				if (type instanceof Span && ((Span) type).hasDelegate()) {
					Span span = (Span) type;

					delegates.put(span.getDelegateContentType(), ModeCache
							.resolveDelegate(this, span.getDelegate()));

					contentTypesList.add(span.getDelegateContentType());
				}
			}
			contentTypes = (String[]) contentTypesList
					.toArray(new String[contentTypesList.size()]);
		}
		return contentTypes;
	}

	/**
	 * 
	 * @param extensions
	 */
	public void appendExtensionsOnto(Set<String> extensions) {
		if (filenameGlob == null)
			return;
		StringBuffer buf = new StringBuffer();
		Stack<Character> charClass = new Stack<Character>();
		boolean inCharClass = false;
		boolean charClassBefore = false;
		for (int i = 0; i < filenameGlob.length(); i++) {
			char c = filenameGlob.charAt(i);
			switch (c) {
			case '*':
			case '.':
			case '{':
			case '}':
				break;
			case '[':
				inCharClass = true;
				charClassBefore = buf.length() == 0;
				break;
			case ']':
				inCharClass = false;
				break;
			case ',':
				extensions.add(buf.toString());
				buf.setLength(0);
				break;
			default:
				if (inCharClass) {
					charClass.push(new Character(c));
					break;
				}
				buf.append(c);
				break;
			}
		}
		if (charClass.size() > 0) {
			String suffixOrPrefix = buf.toString();
			buf.setLength(0);
			while (!charClass.isEmpty()) {
				Character ch = (Character) charClass.pop();
				extensions.add(charClassBefore ? ch + suffixOrPrefix
						: suffixOrPrefix + ch);
			}
			return;
		}
		if (buf.length() > 0)
			extensions.add(buf.toString());
	}

	public String getBeginComment() {
		return beginComment;
	}

	public void setBeginComment(String beginComment) {
		if (beginComment != null)
			this.beginComment = beginComment;
	}

	public String getStopComment() {
		return endComment;
	}

	public void setStopComment(String endComment) {
		if (endComment != null)
			this.endComment = endComment;
	}
	
	public void addProperty(String name, String value) {
		this.properties.put(name, value);
	}
	
}