/**********************************************************************
 Copyright (c) 2000, 2002 IBM Corp. and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Common Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/cpl-v10.html

 Contributors:
 IBM Corporation - Initial implementation
 Klaus Hartlage - www.eclipseproject.de
 **********************************************************************/
package com.rohanclan.afae.editor.indentstrategy;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeEditor;

public class BracketedIndentStrategy extends DefaultIndentStrategy {
	/**
	 * @param editor
	 */
	public BracketedIndentStrategy(AfaeEditor editor) {
		super(editor);
	}

	/**
	 * Handles the insertion of quotes by the user. If the user has opened
	 * quotes then it inserts a closing quote after the opened quote and does
	 * not move the caret. If the user is closing some quotes it steps through
	 * the existing quote.
	 * 
	 * @param doc - The document that the command is being performed in
	 * @param docCommand - the command to modify
	 * @param quoteChar - the quote character that triggered this. This allows us to handle " and ' quotes.
	 * @throws BadLocationException - ack.
	 */
	private void handleQuotes(IDocument doc, DocumentCommand docCommand, char quoteChar) throws BadLocationException {
		int coffset = docCommand.offset;

		//if the offset is below the file len forget it
		if(coffset < 0)
			return;

		//if they typing at the end of the file (or just starting the file) add
		//the new quote
		if(coffset >= doc.getLength()) {
			insertSingleChar(docCommand, quoteChar);
			return;
		}

		//otherwise check to see if we need to add the " and do so if needed
		char nextChar = doc.getChar(coffset);
		
		if(nextChar == quoteChar) {
			stepThrough(docCommand);
			return;
		}
		
		if(!Character.isWhitespace(nextChar)) {
			return;
		}

		insertSingleChar(docCommand, quoteChar);
		return;
	}

	/**
	 * Handles the insertion & step-through of code.
	 * 
	 * @param doc - the document to work upon
	 * @param docCommand - command to modify
	 */
	private void codeInsertion(IDocument doc, DocumentCommand docCommand) {
		char nextChar = ' ';
		char prevChar = ' ';
		char trigChar = ' ';

		// We don't do anything if there isn't any text that's been entered.
		// Basicall we're not interested in anything but the user inserting
		// text.
		if(docCommand.text.length() > 1) {
			// this is probably a paste - quit
			return;
		}

		//Handle a closing chevron.
		if(docCommand.length > 0 && docCommand.text.length() == 0) {
			trigChar = '\b';
		} else {
			trigChar = docCommand.text.charAt(0);
		}
		
		try {
			if(docCommand.offset < doc.getLength()) {
				nextChar = doc.getChar(docCommand.offset);
			}
			if(docCommand.offset > 0) {
				int toPos = docCommand.offset - 2;
				
				if(toPos >= 0)
					prevChar = doc.getChar(docCommand.offset - 2);
			}
			
			switch (trigChar) {
				
				case '[':
					if (Character.isLetterOrDigit(nextChar) || nextChar == '_')
						break;
					else
						insertSingleChar(docCommand, ']');
				break;
				
				case '\"':
					handleQuotes(doc, docCommand, '"');
				break;
				
				case '(':
					if (Character.isLetterOrDigit(nextChar) || nextChar == '_')
						break;
					else
						insertSingleChar(docCommand, ')');
				break;
				
				case ')':
					if (nextChar == ')')
						stepThrough(docCommand);
				break;
				
				case ']':
					if (nextChar == ']')
						stepThrough(docCommand);
				break;
				
				case '{':
					insertSingleChar(docCommand, '}');
				break;
				
				case '\b':
					//check to see if they are backspacing over an auto closing thing
					//we made and try to remove the other item if it looks like it.
					prevChar = doc.getChar(docCommand.offset);
					nextChar = doc.getChar(docCommand.offset+1);
					if(
						(prevChar == '(' && nextChar == ')') ||
						(prevChar == '[' && nextChar == ']') ||
						(prevChar == '{' && nextChar == '}') ||
						(prevChar == '\"' && nextChar == '\"') ||
						(prevChar == '\'' && nextChar == '\'')
					) {
						doc.replace(docCommand.offset+1, 1, "");
					}
				break;
				
				
				case '\n':
					//because they hit enter the prevChar wont be correct. We have
					//to go 1 back to get the real previous char. So try
					try {
						prevChar = doc.getChar(docCommand.offset - 1);
					} catch (BadLocationException ex) {
						AfaePlugin.logError("", ex, BracketedIndentStrategy.class);
						return;
					}
					
					if(nextChar == '}' && prevChar == '{') { 						
						int indent_amount = smartIndentAfterNewLine(doc, docCommand);
						insertSingleChar(docCommand, '\t');
						insertSingleChar(docCommand, '\n');
						smartIndentAfterNewLine(doc, docCommand);
						
						//since the shiftCaret stuff wont take into account the newly added
						//text (the offsets only count the text that was there before the
						//command runs) we'll use the editor to position the caret after the
						//insert.
						//docCommand.shiftsCaret = true;
						
						//start from the insert point, add the indent amount and +1 for
						//the new tab we added (the insert point will have the original 
						//newline)
						final int startpos = docCommand.caretOffset + indent_amount + 1;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								//editor.updateFoldingStructure(fPositions);
								editor.setHighlightRange(startpos, 0, true);
							}
						});
					}
				break;
			}
		} catch (BadLocationException ex) {
			AfaePlugin.logError("", ex, BracketedIndentStrategy.class);
		} catch (Exception e) {
			AfaePlugin.logError("", e, BracketedIndentStrategy.class);
		}
	}

	/*
	 * (non-Javadoc) Method declared on IAutoIndentStrategy
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		codeInsertion(d, c);

		if (c.length == 0 && c.text != null && endsWithDelimiter(d, c.text)) {
			smartIndentAfterNewLine(d, c);
		} else if ("{".equals(c.text)) {
			smartIndentAfterNewLine(d, c);
		} else if ("}".equals(c.text)) {
			smartInsertAfterBracket(d, c);
		} else if (c.text.equals("\t")) {
			singleLineIndent(d, c);
		}
	}

	/**
	 * Returns whether or not the text ends with one of the given search
	 * strings.
	 */
	private boolean endsWithDelimiter(IDocument d, String txt) {
		String[] delimiters = d.getLegalLineDelimiters();

		for (int i = 0; i < delimiters.length; i++) {
			if (txt.endsWith(delimiters[i]))
				return true;
		}
		return false;
	}

	/**
	 * Returns the line number of the next bracket after end.
	 * 
	 * @returns the line number of the next matching bracket after end
	 * @param document - the document being parsed
	 * @param line - the line to start searching back from
	 * @param end - the end position to search back from
	 * @param closingBracketIncrease - the number of brackets to skip
	 */
	protected int findMatchingOpenBracket(IDocument document, int line,	int end, int closingBracketIncrease) throws BadLocationException {
		int start = document.getLineOffset(line);
		int brackcount = getBracketCount(document, start, end, false)
				- closingBracketIncrease;

		// sum up the brackets counts of each line (closing brackets count
		// negative,
		// opening positive) until we find a line the brings the count to zero
		while (brackcount < 0) {
			line--;
			if (line < 0) {
				return -1;
			}

			start = document.getLineOffset(line);
			end = start + document.getLineLength(line) - 1;
			brackcount += getBracketCount(document, start, end, false);
		}
		return line;
	}

	/**
	 * Returns the bracket value of a section of text. Closing brackets have a
	 * value of -1 and open brackets have a value of 1.
	 * 
	 * @returns the line number of the next matching bracket after end
	 * @param document - the document being parsed
	 * @param start - the start position for the search
	 * @param end - the end position for the search
	 * @param ignoreCloseBrackets - whether or not to ignore closing brackets in the count
	 */
	private int getBracketCount(IDocument document, int start, int end,	boolean ignoreCloseBrackets) throws BadLocationException {
		int begin = start;
		int bracketcount = 0;

		while (begin < end) {
			char curr = document.getChar(begin);
			begin++;
			switch (curr) {
			case '/':
				if (begin < end) {
					char next = document.getChar(begin);
					if (next == '*') {
						// a comment starts, advance to the comment end
						begin = getCommentEnd(document, begin + 1, end);
					} else if (next == '/') {
						// '//'-comment: nothing to do anymore on this line
						begin = end;
					}
				}
				break;
			case '*':
				if (begin < end) {
					char next = document.getChar(begin);
					if (next == '/') {
						// we have been in a comment: forget what we read before
						bracketcount = 0;
						begin++;
					}
				}
				break;
			case '{':
				bracketcount++;
				ignoreCloseBrackets = false;
				break;
			case '}':
				if (!ignoreCloseBrackets) {
					bracketcount--;
				}
				break;
			case '"':
			case '\'':
				begin = getStringEnd(document, begin, end, curr);
				break;
			default:
			}
		}
		return bracketcount;
	}

	/**
	 * Returns the end position a comment starting at pos.
	 * 
	 * @returns the end position a comment starting at pos
	 * @param document -
	 *            the document being parsed
	 * @param position -
	 *            the start position for the search
	 * @param end -
	 *            the end position for the search
	 */
	private int getCommentEnd(IDocument document, int position, int end)
			throws BadLocationException {
		int currentPosition = position;

		while (currentPosition < end) {
			char curr = document.getChar(currentPosition);
			currentPosition++;
			if (curr == '*') {
				if (currentPosition < end
						&& document.getChar(currentPosition) == '/') {
					return currentPosition + 1;
				}
			}
		}
		return end;
	}

	/**
	 * Returns the String at line with the leading whitespace removed.
	 * 
	 * @returns the String at line with the leading whitespace removed.
	 * @param document - the document being parsed
	 * @param line - the line being searched
	 */
	protected String getIndentOfLine(IDocument document, int line)	throws BadLocationException {
		if (line > -1) {
			int start = document.getLineOffset(line);
			int end = start + document.getLineLength(line) - 1;
			int whiteend = findEndOfWhiteSpace(document, start, end);
			return document.get(start, whiteend - start);
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Returns the String at line with the leading whitespace removed.
	 * 
	 * @returns the String at line with the leading whitespace removed.
	 * @param document -
	 *            the document being parsed
	 * @param line -
	 *            the line being searched
	 */
	protected String getIndentLevelOfLine(IDocument document, int line)	throws BadLocationException {
		if (line > -1) {
			int start = document.getLineOffset(line);
			int end = start + document.getLineLength(line) - 1;
			int whiteend = findEndOfWhiteSpace(document, start, end);

			AfaePlugin.logDebug("items: " + start + " " + (whiteend - start),
					null, this.getClass());

			// if(whiteend > (start+1))
			// {
			return document.get(start, (whiteend - start));
			// }
			// else
			// {
			// return "";
			// }
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Returns the position of the character in the document after position.
	 * 
	 * @returns the next location of character.
	 * @param document -
	 *            the document being parsed
	 * @param position -
	 *            the position to start searching from
	 * @param end -
	 *            the end of the document
	 * @param character -
	 *            the character you are trying to match
	 */
	private int getStringEnd(IDocument document, int position, int end,	char character) throws BadLocationException {
		int currentPosition = position;

		while (currentPosition < end) {
			char currentCharacter = document.getChar(currentPosition);
			currentPosition++;
			if (currentCharacter == '\\') {
				// ignore escaped characters
				currentPosition++;
			} else if (currentCharacter == character) {
				return currentPosition;
			}
		}
		return end;
	}

	/**
	 * Set the indent of a new line based on the command provided in the
	 * supplied document.
	 * 
	 * @param document - the document being parsed
	 * @param command - the command being performed
	 * @return the count of the number of chars used for whitespace padding
	 */
	protected int smartIndentAfterNewLine(IDocument document, DocumentCommand command) {
		int docLength = document.getLength();
		
		int start = 0;
		int whiteend = 0;
		
		if (command.offset == -1 || docLength == 0)
			return 0;

		try {
			int p = (command.offset == docLength ? command.offset - 1 : command.offset);
			int line = document.getLineOfOffset(p);

			StringBuffer buf = new StringBuffer(command.text);
			
			if (command.offset < docLength && document.getChar(command.offset) == '}') {
				int indLine = findMatchingOpenBracket(document, line, command.offset, 0);
				if (indLine == -1) {
					indLine = line;
				}
				String whitespace = getIndentOfLine(document, indLine);
				buf.append(whitespace);
				whiteend = whitespace.length();
			} else {
				start = document.getLineOffset(line);
				whiteend = findEndOfWhiteSpace(document, start, command.offset);
				buf.append(document.get(start, whiteend - start));
				
				if (getBracketCount(document, start, command.offset, true) > 0) {
					buf.append('\t');
				}
			}
			command.text = buf.toString();
		} catch (BadLocationException excp) {
			AfaePlugin.logError("", excp, BracketedIndentStrategy.class);
		}
		
		return whiteend - start;
	}

	/**
	 * Set the indent of a bracket based on the command provided in the supplied
	 * document.
	 * 
	 * @param document -
	 *            the document being parsed
	 * @param command -
	 *            the command being performed
	 */
	protected void smartInsertAfterBracket(IDocument document, DocumentCommand command) {
		if (command.offset == -1 || document.getLength() == 0)
			return;

		try {
			int p = (command.offset == document.getLength() ? command.offset - 1 : command.offset);
			int line = document.getLineOfOffset(p);
			int start = document.getLineOffset(line);
			int whiteend = findEndOfWhiteSpace(document, start, command.offset);

			// shift only when line does not contain any text up to the closing
			// bracket
			if (whiteend == command.offset) {
				// evaluate the line with the opening bracket that matches out
				// closing bracket
				int indLine = findMatchingOpenBracket(document, line,
						command.offset, 1);
				if (indLine != -1 && indLine != line) {
					// take the indent of the found line
					StringBuffer replaceText = new StringBuffer(
							getIndentOfLine(document, indLine));
					// add the rest of the current line including the just added
					// close bracket
					replaceText.append(document.get(whiteend, command.offset
							- whiteend));
					replaceText.append(command.text);
					// modify document command
					command.length = command.offset - start;
					command.offset = start;
					command.text = replaceText.toString();
				}
			}
		} catch (BadLocationException excp) {
			AfaePlugin.logError("", excp, BracketedIndentStrategy.class);
		}
	}
}