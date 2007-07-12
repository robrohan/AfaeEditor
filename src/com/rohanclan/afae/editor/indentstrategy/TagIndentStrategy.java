/*
 * Created on July 1st, 2004.
 *
 * The MIT License
 * Copyright (c) 2004 Oliver Tupman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.rohanclan.afae.editor.indentstrategy;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeEditor;
import com.rohanclan.afae.editor.AfaeEditorTools;
import com.rohanclan.afae.prefs.IPreferenceConstants;

/**
 * This represents a tag-based auto-indent strategy. It not only does the
 * auto-indenting, but it also does the auto-closing & step-through of various
 * characters.
 * 
 * @author Oliver Tupman
 */
public class TagIndentStrategy extends DefaultIndentStrategy {
	/*
	 * <cffunction name="fred> : true 
	 * <cffunction name="fred" returntype="boolean"> : true
	 * 
	 * </cfoutput> : false (1) <cfoutput> </cfoutput> > : false (2)
	 */
	private static final int AFTEROPENTAG_AFTER = 0;

	private static final int AFTEROPENTAG_CLOSER = 1;

	private static final int AFTEROPENTAG_NOTAFTER = 2;
	
	/** Indent when the tag is closed and a end tag is inserted */
	public static final int INDENT_ONTAGCLOSE = 0;

	/**
	 * Indent only when the user presses enter at this point
	 * &lt;cfif&gt;&lt;/cfif&gt;
	 */
	public static final int INDENT_ONCLOSEDTAGENTER = 1;

	/** Don't indent */
	public static final int INDENT_DONTDOIT = 2;

	/** Auto-close double quotes */
	private boolean autoClose_DoubleQuotes = true;

	/** Auto-close single quotes */
	private boolean autoClose_SingleQuotes = true;

	/** Auto-close tags */
	//private boolean autoClose_Tags = false;
	
	/** Auto-insert a closing tag */
	private boolean autoInsert_CloseTags = true;

	/** When to trigger the auto-indent strategy when the user is in a tag */
	//private int autoIndent_OnTagClose = INDENT_ONCLOSEDTAGENTER;
	
	/**
	 * @param editor
	 */
	public TagIndentStrategy(AfaeEditor editor) {
		super(editor);
	}

	/**
	 * Returns the closing tag based upon the previous tag entered. So enter
	 * <cf_fred> and the closing tag returned is "cf_fred". The calling function
	 * can sort out the chevrons.
	 * 
	 * @param currDoc -
	 *            Current document that we're in
	 * @param documentOffset -
	 *            the offset in the document
	 * @return the string name of the tag sans chevrons, otherwise a blank
	 *         string
	 */
	private String getClosingTag(IDocument currDoc, int documentOffset) {
		String preTrigger;
		try {
			preTrigger = currDoc.get(0, documentOffset - 1);
		} catch (BadLocationException ex) {
			AfaePlugin.logError(
				"TagIndentStrategy::getClosingTag() - Caught bad location for preTrigger", 
				ex, 
				TagIndentStrategy.class
			);
			return "";
		}
		
		int opener = preTrigger.lastIndexOf('<');
		if (opener == -1)
			return "";

		String startOfTag = preTrigger.substring(opener);
		int i;
		for (i = 1; i < startOfTag.length(); i++) {
			char currChar = startOfTag.charAt(i);
			// for namespace tags, and "oddly" named tags
			if (!(Character.isLetterOrDigit(currChar) || currChar == ':'
					|| currChar == '_' || currChar == '-' || currChar == '.')) {
				break;
			}
		}

		return startOfTag.substring(1, i);
	}

	/**
	 * Modifies the DocumentCommand to handle the user closing a single tag
	 * (i.e. &lt;nop/&gt;)
	 * 
	 * @param command -
	 *            the document command to modify
	 * @return the modified document command.
	 */
	private DocumentCommand singleTagTakeAction(IDocument doc, DocumentCommand command) {
		char nextChar = ' ';
		if (command.offset < doc.getLength()) {
			try {
				nextChar = doc.getChar(command.offset);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		if (nextChar != '>') {
			return command;
		}

		stepThrough(command);
		return command;
	}

	/**
	 * 
	 * @param doc
	 * @param docCommand
	 * @param tagName
	 * @return
	 */
	private boolean tagIsSingle(IDocument doc, DocumentCommand docCommand,
			String tagName) {
		return false;
	}

	/**
	 * Handles a closing, non-single tag. It will insert two lines. The first
	 * will be the next line that the cursor will be upon and will be indented
	 * by the opener tag's indent + 1 tab. The 2nd line will be on the same
	 * level as the opener and will contain the closing tag.
	 * 
	 * @param doc - the document that this action is taking place within
	 * @param docCommand - the document command to modify
	 * @return the modified document command
	 * @throws BadLocationException
	 */
	private DocumentCommand doCloser(IDocument doc, DocumentCommand docCommand) throws BadLocationException {
		//char lastChar = ' ';
		char nextChar = ' ';
		boolean autoCloseTag = this.autoInsert_CloseTags;
		boolean isSingleTag = false;

		//if (docCommand.offset >= 0)
		//	lastChar = doc.getChar(docCommand.offset - 1);

		if (docCommand.offset < doc.getLength())
			nextChar = doc.getChar(docCommand.offset);

		String closingTag = getClosingTag(doc, docCommand.offset + 1);

		// isSingleTag = tagIsSingle(doc,docCommand,closingTag);
		isSingleTag = false;

		autoCloseTag = !tagIsSingle(doc, docCommand, closingTag)
				&& autoCloseTag;

		// If the user hasn't got auto-insertion of closing chevrons on, then
		// add a closing chevron onto our close tag (handled otherwise due to
		// the fact we're inserting code IN the tag itself!
		if (!autoCloseTag || nextChar != '>')
			// System.out.println("Next char is a " + nextChar);
			// if(!autoCloseTag)
			closingTag += ">";

		closingTag = "</" + closingTag;

		//if (this.autoIndent_OnTagClose == INDENT_ONTAGCLOSE)
			// User wants us to insert a CR and indent.
		//	doInBetweenTagIndent(doc, docCommand, lastChar);
		//else {
			try {
				if (docCommand.offset != doc.getLength()
						&& doc.getChar(docCommand.offset) == '>') {
					stepThrough(docCommand);
				} else {
					docCommand.caretOffset = docCommand.offset + 1;
					docCommand.shiftsCaret = false;
				}
				// If we're auto-closing tags then we need a closing chevron.
				if (autoCloseTag && !closingTag.endsWith(">"))
					closingTag += ">";

				// docCommand.caretOffset = docCommand.offset;
				// docCommand.shiftsCaret = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}

		// Use the first line's whitespace and insert the closing tag.
		// if the tag went wrong somehow and we get an empty tag
		// (like in <?php -> can cause this to trigger), dont show the
		// incorrect, empty tag
		if (!isSingleTag && !closingTag.equals("</>"))
			docCommand.text += closingTag;
		return docCommand;
	}

	/**
	 * @param doc
	 * @param docCommand
	 * @param lastChar
	 * @throws BadLocationException
	 */
	/* private void doInBetweenTagIndent(IDocument doc, DocumentCommand docCommand, char lastChar) throws BadLocationException {
		int openChevron = doc.get(0, docCommand.offset).lastIndexOf('<');
		if (openChevron == -1)
			return;

		// String whiteSpace = getPrevLineWhiteSpace(doc,openChevron);
		String whiteSpace = AfaeEditorTools.getPrevLineWhiteSpace(doc, openChevron);

		docCommand.caretOffset = docCommand.offset + 3 + whiteSpace.length();

		if (lastChar == '>') {
			docCommand.text = "";
			docCommand.offset++;
		}

		if (whiteSpace.length() == 0)
			docCommand.caretOffset--;

		docCommand.shiftsCaret = false;

		// End the current line the user is on.
		docCommand.text += "\n";
		if (lastChar == '>')
			docCommand.text += "\t";

		// Create the whitespace for the next line (the user will end up on this one)
		docCommand.text += whiteSpace + "\t" + "\n";
		docCommand.text += whiteSpace;
	} */

	private int afterOpenTag(IDocument doc, int offset) {
		int pos = offset - 1;
		try {
			for (; pos > 0; pos--) {
				char currChar = doc.getChar(pos);
				if (currChar == '>') // Condition (2)
					return AFTEROPENTAG_NOTAFTER;
				else if (currChar == '/') {
					if (doc.getChar(pos - 1) == '<') // Condition (1)
						return AFTEROPENTAG_CLOSER;
				} else if (currChar == '<') {
					return AFTEROPENTAG_AFTER;
				}
			}
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
		return AFTEROPENTAG_AFTER;
	}

	/**
	 * The method called by the editor.
	 */
	public void customizeDocumentCommand(IDocument doc, DocumentCommand docCommand) {
		try {
			// this is really similar to bracket indent...
			if (docCommand.text.length() > 1) {
				// this is probably a paste - quit
				return;
			}
			
			char beforeLastChar = ' ';
			char firstCommandChar = ' ';

			if (docCommand.offset - 1 >= 0) {
				beforeLastChar = doc.getChar(docCommand.offset - 1);
			}
			
			if(docCommand.length > 0 && docCommand.text.length() == 0) {
				firstCommandChar = '\b';
			} else {
				firstCommandChar = docCommand.text.charAt(0);
			}
			
			char prevChar = doc.getChar(docCommand.offset - 1);
			char nextChar = doc.getChar(docCommand.offset + 1);
			
			switch (firstCommandChar) {
				case '>':
					nextChar = doc.getChar(docCommand.offset);
					//System.err.println("]" + prevChar + " " + nextChar + "[");
					if (!this.autoInsert_CloseTags) {
						if (doc.getLength() > docCommand.offset && nextChar == '>')
							stepThrough(docCommand);
					} else {
						//this was at attempt to keep the end tag from getting made
						//more than once, but it stops proper tag closer as well.
						//if(doc.getChar(docCommand.offset+1) == '<')
						//	stepThrough(docCommand);
						
						//only auto close if there is nothing after the >. It's a pain
						//when you are say adding a <b> tag to some text but it auto
						//closes. For example "<b>|sometext here" it should not auto close
						//in that use case
						if(!Character.isLetterOrDigit(nextChar) && nextChar != '_' && nextChar != '<')
							handleClosingChevron(doc, docCommand, beforeLastChar);
					}
					return;
	
				case '<':
					if (!this.autoInsert_CloseTags) {
						insertSingleChar(docCommand, '>');
					} else {
						handleOpenChevron(docCommand);
					}
					return;
				
				case '\"':
					if (!this.autoClose_DoubleQuotes) {
						return; // User doesn't want us to do this
					} else {
						handleQuotes(doc, docCommand, firstCommandChar);
						return;
					}
				
				case '\t': // handle tabs
					singleLineIndent(doc, docCommand);
					return;
				
				case '\b':
					//since this is a backspace  update the prevChar
					//to not be the backspace char, but the actual one
					prevChar = doc.getChar(docCommand.offset);
					if(
						(prevChar == '(' && nextChar == ')') ||
						(prevChar == '[' && nextChar == ']') ||
						(prevChar == '{' && nextChar == '}') ||
						(prevChar == '\"' && nextChar == '\"') ||
						(prevChar == '\'' && nextChar == '\'')
					) {
						doc.replace(docCommand.offset+1, 1, "");
					}
					return;
					
				default:
					// Check to make sure that the text entered isn't a CF/CRLF and
					// that
					// there is actually data in the command. Otherwise cop out.
					if ((docCommand.text.compareTo("\r\n") != 0 
							&& docCommand.text.compareTo("\n") != 0)
							|| docCommand.length != 0) 
					{
						// System.out..println("TagIndentStrategy::customizeDocument()
						// - In fall out");
						// attempt to register a default behavior
						super.customizeDocumentCommand(doc, docCommand);
						return;
					}
					break;
			}

			// Here the user must've pressed enter to go to a new line.
			// So we have two options:
			// 1) They're just pressing in between two tags OR
			// 2) They've pressed enter WITHIN a tag (i.e. <cffred name="blah"
			// [ENTER]>
			if (isEnterInTag(doc, docCommand)) {
				handleEnterInTag(doc, docCommand);
			} else {
				String prevLineWhitespace = AfaeEditorTools.getPrevLineWhiteSpace(doc, docCommand.offset);
				docCommand.text += guessNewIndentWhitespace(prevLineWhitespace);
			}
		} catch (BadLocationException ex) {
			AfaePlugin.logError(
				"TagIndentStrategy::customizeDocumentCommand() - Caught BadLocationException", 
				ex, 
				TagIndentStrategy.class
			);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * When a client hits enter between tags the previous line is looked at to
	 * get the indent depth - if the file was made with "tabs as spaces" and the
	 * new person editing uses proper tabs then the new indents should be tabs
	 * not spaces (or so the bug report goes :)
	 * 
	 * This looks at the preferences and switches the tab/space indent as best
	 * it can. you run into trouble with things like is 6 spaces one tab or 2.
	 * 
	 * @param guessInfo
	 * @return
	 */
	public String guessNewIndentWhitespace(String previousWhitespace) {
		int indentWidth = Integer.parseInt(
				AfaePlugin.getDefault().getPreferenceStore().getString(
					IPreferenceConstants.P_TAB_WIDTH
				)
			);
		
		if (previousWhitespace == null || previousWhitespace.length() <= 0) {
			return "";
		}

		// if we are now set to "tabs for tabs"..
		if (this.getIndentString().compareTo("\t") == 0) {
			// if the last whitespace is a tab too assume we are all good
			if (previousWhitespace.charAt(0) == '\t') {
				return previousWhitespace;
			}
			// last indent line was spaces so time to guess how many tabs that
			// is
			else if (previousWhitespace.charAt(0) == ' ') {
				// use the tab width setting as a guide
				int numtabs = previousWhitespace.length() / indentWidth;
				StringBuffer sb = new StringBuffer();
				for (int x = 0; x < numtabs; x++) {
					sb.append('\t');
				}
				return sb.toString();
			} else {
				throw new IllegalArgumentException(
						"whitespaces guesser got non whitespace: ]"
								+ previousWhitespace + "[");
			}
		}
		// we are set to spaces for tabs
		else {
			// if the last whitespace is a space too assume we are all good
			if (previousWhitespace.charAt(0) == ' ') {
				return previousWhitespace;
			} else if (previousWhitespace.charAt(0) == '\t') {
				int spacelen = previousWhitespace.length() * indentWidth;
				StringBuffer sb = new StringBuffer();
				for (int x = 0; x < spacelen; x++) {
					sb.append(' ');
				}
				return sb.toString();
			} else {
				throw new IllegalArgumentException(
					"whitespaces guesser got non whitespace: ]"
					+ previousWhitespace + "["
				);
			}
		}
	}

	/**
	 * Handles the user typing in a closing chevron. s
	 * 
	 * @param doc
	 * @param docCommand
	 * @param beforeLastChar
	 * @throws BadLocationException
	 */
	private void handleClosingChevron(IDocument doc,
			DocumentCommand docCommand, char beforeLastChar)
			throws BadLocationException {
		// Have we not got a tag name
		if (beforeLastChar == '<')
			return;
		else if (beforeLastChar == '/') { // A self-closer, i.e. : <br/>
			// singleTagTakeAction(doc, docCommand);
			if (this.autoInsert_CloseTags) {
				singleTagTakeAction(doc, docCommand);
			} else
				docCommand.doit = true;
		} else {
			// Got a '>', make sure that it's not a random closer and if not
			// close the tag.
			switch (afterOpenTag(doc, docCommand.offset)) {
				case AFTEROPENTAG_AFTER:
					doCloser(doc, docCommand);
					break;
				case AFTEROPENTAG_CLOSER:
					char currChar = doc.getChar(docCommand.offset);
					if (currChar == '>')
						stepThrough(docCommand);
					break;
				case AFTEROPENTAG_NOTAFTER:
				default:
					break;
			}
		}
		return;
	}

	/**
	 * Performs the required operations to provide indenting when the user
	 * presses enter inside a tag.
	 * 
	 * @param docCommand - the document command to work up.
	 */
	private void handleEnterInTag(IDocument doc, DocumentCommand docCommand) {
		try {
			int currLine = doc.getLineOfOffset(docCommand.offset);

			String lineDelim = doc.getLineDelimiter(currLine);
			if (lineDelim == null)
				lineDelim = "\r\n";

			// Now we just need to work out how much indentation to do...
			// int posForIndent = findEndOfTagNameOrStartOfAttribute(doc.get(),
			// docCommand.offset-1);
			String prefix = AfaeEditorTools.getPrevLineWhiteSpace(doc, docCommand.offset);
			docCommand.text += guessNewIndentWhitespace(prefix);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
			return;
		} catch (Exception e) {
			//Catch-all. One would hope we never reach here!
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Has the user pressed enter from within a tag (i.e. they were editing the
	 * attributes)?
	 * 
	 * @param doc -  Document that we're operating on
	 * @param command - the command
	 * @return - True: yes, enter pressed in a tag. False: Nope, outside of a tag.
	 */
	private boolean isEnterInTag(IDocument doc, DocumentCommand command) {
		int position = command.offset - 1;
		String docData = doc.get();
		boolean openerFound = false;
		// First, search backwards. We should hit a '<' before we hit a '>'.
		int i = position;
		try {
			for (; i > 0; i--) {
				if (docData.charAt(i) == '>')
					return false; // Found closing chevron, die now.
				// TODO: Will kill if closing chevron is in quotes!

				if (docData.charAt(i) == '<') {
					openerFound = true;
					break;
				}
			}
		} catch (Exception e) {
			System.err
					.println("TagIndentStrategy::isEnterInTag() - Caught exception \'"
							+ e.getMessage() + "\'. Dumping.");
			e.printStackTrace();
			return false;
		}

		return openerFound;
	}

	/**
	 * Handles the opening of a chevron. Basically it inserts a matching closing
	 * '>' and does not shift the caret.
	 * 
	 * @param docCommand -
	 *            the document command to modify
	 */
	private void handleOpenChevron(DocumentCommand docCommand) {
		docCommand.text += ">";
		docCommand.shiftsCaret = false;
		docCommand.caretOffset = docCommand.offset + 1;
	}

	/**
	 * Handles the insertion of quotes by the user. If the user has opened
	 * quotes then it inserts a closing quote after the opened quote and does
	 * not move the caret. If the user is closing some quotes it steps through
	 * the existing quote.
	 * 
	 * @param doc - The document that the command is being performed in
	 * @param docCommand - the command to modify
	 * @param quoteChar -  the quote character that triggered this. This allows us to
	 *            handle " and ' quotes.
	 * @throws BadLocationException - ack.
	 */
	private void handleQuotes(IDocument doc, DocumentCommand docCommand,
			char quoteChar) throws BadLocationException {
		char nextChar = (char) 0;

		try {
			nextChar = doc.getChar(docCommand.offset);
		} catch (BadLocationException bex) {
			// do nothing
		}

		// don't do it unless it is not around some text || nextChar == '>' 
		if (Character.isLetterOrDigit(nextChar) || nextChar == '<') {
			return;
		}

		if (nextChar == quoteChar) {
			docCommand.text = "";
			docCommand.shiftsCaret = false;
			docCommand.caretOffset = docCommand.offset + 1;
			return;
		}

		docCommand.text += quoteChar;
		docCommand.caretOffset = docCommand.offset + 1;
		docCommand.shiftsCaret = false;
		return;
	}

	/**
	 * @return Returns the autoClose_DoubleQuotes.
	 */
	public boolean isAutoClose_DoubleQuotes() {
		return autoClose_DoubleQuotes;
	}

	/**
	 * @param autoClose_DoubleQuotes
	 *            The autoClose_DoubleQuotes to set.
	 */
	public void setAutoClose_DoubleQuotes(boolean autoClose_DoubleQuotes) {
		this.autoClose_DoubleQuotes = autoClose_DoubleQuotes;
	}

	/**
	 * @return Returns the autoClose_SingleQuotes.
	 */
	public boolean isAutoClose_SingleQuotes() {
		return autoClose_SingleQuotes;
	}

	/**
	 * @param autoClose_SingleQuotes
	 *            The autoClose_SingleQuotes to set.
	 */
	public void setAutoClose_SingleQuotes(boolean autoClose_SingleQuotes) {
		this.autoClose_SingleQuotes = autoClose_SingleQuotes;
	}

	/**
	 * @return Returns the autoInsert_CloseTags.
	 */
	public boolean isAutoInsert_CloseTags() {
		return autoInsert_CloseTags;
	}

	/**
	 * @param autoInsert_CloseTags
	 *            The autoInsert_CloseTags to set.
	 */
	public void setAutoInsert_CloseTags(boolean autoInsert_CloseTags) {
		this.autoInsert_CloseTags = autoInsert_CloseTags;
	}
}
