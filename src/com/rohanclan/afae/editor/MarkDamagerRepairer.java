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
package com.rohanclan.afae.editor;

// import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.rules.LToken;

/**
 * 
 * @author robrohan
 */
public class MarkDamagerRepairer extends DefaultDamagerRepairer {
	/*
	 * @see org.eclipse.jface.text.rules.DefaultDamagerRepairer#DefaultDamagerRepairer(ITokenScanner,
	 *      TextAttribute)
	 * @deprecated
	 */
	/*
	 * public MarkDamagerRepairer(ITokenScanner scanner, TextAttribute
	 * defaultTextAttribute) { super(scanner, defaultTextAttribute); }
	 */

	public MarkDamagerRepairer(ITokenScanner scanner) {
		super(scanner);
	}

	/*
	 * 
	 */
	public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
		AfaePlugin.logDebug("going into createPresentation", null, MarkDamagerRepairer.class);
		// AfaePlugin.logDebug("damage type: " + damage.getType(),null,
		// MarkDamagerRepairer.class);

		if (fScanner == null) {
			// will be removed if deprecated constructor will be removed
			addRange(presentation, damage.getOffset(), damage.getLength(), fDefaultTextAttribute);
			return;
		}

		int lastStart = damage.getOffset();
		int length = 0;
		IToken lastToken = Token.UNDEFINED;
		TextAttribute lastAttribute = getTokenTextAttribute(lastToken);

		// setup the scanner to just mess with the modified range
		fScanner.setRange(fDocument, lastStart, damage.getLength());

		/*
		 * AfaePlugin.logDebug( "going into token painting loop... s: " +
		 * lastStart + " l:" + length, null, MarkDamagerRepairer.class );
		 */

		// loop over till end of file... um...
		while (true) {
			IToken token = fScanner.nextToken();

			if (token.isEOF())
				break;

			// get the color, etc for the passed token
			TextAttribute attribute = getTokenTextAttribute(token);
			
			// if the last attribute is the same as this one move along?
			if (lastAttribute != null && lastAttribute.equals(attribute)) {
				length += fScanner.getTokenLength();
			} else {
				// add the presentation for the section we are playing with
				addRange(presentation, lastStart, length, lastAttribute);
				lastToken = token;
				lastAttribute = attribute;
				lastStart = fScanner.getTokenOffset();
				length = fScanner.getTokenLength();
			}
		}

		/*
		 * String currentContentType =""; String lastContentType = ""; String
		 * currentText = "";
		 */

		// get the contenttypes
		/*
		 * try { currentContentType = damage.getType(); lastContentType =
		 * fDocument.getContentType(damage.getOffset()-1); currentText =
		 * fDocument.get(damage.getOffset(), damage.getLength()); }
		 * catch(BadLocationException be) { AfaePlugin.logError("getting last
		 * token type",be, MarkDamagerRepairer.class); }
		 */

		// if the last token we hit in that loop above is a previous rule type
		// token
		// go backwards and paint the items before this (like how a ( makes the
		// word
		// before it a function
		int delta = 0;
		int offset = 0;
		if (lastToken instanceof LToken) {
			/*
			 * AfaePlugin.logDebug("painting backwards ",null,
			 * MarkDamagerRepairer.class); AfaePlugin.logDebug("type: " +
			 * currentContentType,null, MarkDamagerRepairer.class);
			 * 
			 * AfaePlugin.logDebug("last type: " + lastContentType,null,
			 * MarkDamagerRepairer.class); AfaePlugin.logDebug("text: " +
			 * currentText,null, MarkDamagerRepairer.class);
			 */
			LToken token = (LToken) lastToken;
			delta = token.isPrevious() ? token.getLength() : 0;
			offset = token.isPrevious() ? 0 : token.getLength();
		}

		// if(!currentContentType.equals(lastContentType))
		// {
		addRange(presentation, lastStart + offset, length - delta, lastAttribute);
		// }
	}
}