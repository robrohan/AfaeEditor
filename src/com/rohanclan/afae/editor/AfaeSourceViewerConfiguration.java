/*
 * Copyright (c) 2005 Rob Rohan, Stephen Milligan, Oliver Tupman
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.indentstrategy.BracketedIndentStrategy;
import com.rohanclan.afae.editor.indentstrategy.DefaultIndentStrategy;
import com.rohanclan.afae.editor.indentstrategy.TagIndentStrategy;
import com.rohanclan.afae.modes.IModeConstants;
import com.rohanclan.afae.modes.Mode;
import com.rohanclan.afae.modes.Rule;
import com.rohanclan.afae.modes.TextSequence;
import com.rohanclan.afae.modes.Type;
import com.rohanclan.afae.prefs.ColorsPreferencePage;
import com.rohanclan.afae.prefs.IPreferenceConstants;
import com.rohanclan.afae.rules.CasedWordRule;
import com.rohanclan.afae.rules.ColorManager;
import com.rohanclan.afae.rules.ColoringWhitespaceDetector;
import com.rohanclan.afae.rules.ColoringWordDetector;
import com.rohanclan.afae.rules.ITokenFactory;
import com.rohanclan.afae.rules.LToken;

/**
 * Configures the viewable document items. Setup rules for modes (keywords and 
 * sequences) does tabs and color items too.
 * @author robrohan
 */
public class AfaeSourceViewerConfiguration extends SourceViewerConfiguration implements IPreferenceConstants, IModeConstants {
	protected ColorManager colorManager;
	
	protected IAutoEditStrategy bracketIndentStrategy;
	protected IAutoEditStrategy chevronIndentStrategy;
	
	protected Map<String, IToken> tokenMap;
	//protected Map markTokenMap;
	
	private Mode mode;
	private PreferenceListener prefListener;
	
	public static final String MARK_SUFFIX = "+mark";
	
	class PreferenceListener implements IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			adaptToPreferenceChange(event);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	public AfaeSourceViewerConfiguration(ColorManager colorManager, AfaeEditor editor) {
		super();
		this.colorManager = colorManager;
		
		tokenMap = new HashMap<String, IToken>();
		
		int tabWidth = getPreferenceStore().getInt(IPreferenceConstants.P_EDITOR_TAB_WIDTH);
		boolean useSpaces = getPreferenceStore().getBoolean(IPreferenceConstants.P_EDITOR_SPACES_FOR_TABS);
		
		bracketIndentStrategy = new BracketedIndentStrategy(editor);
		((DefaultIndentStrategy)bracketIndentStrategy).setIndentString(tabWidth, useSpaces);
		chevronIndentStrategy = new TagIndentStrategy(editor);
		((DefaultIndentStrategy)chevronIndentStrategy).setIndentString(tabWidth, useSpaces);
		
		prefListener = new PreferenceListener();
		
		if(AfaePlugin.getDefault() == null) 
			return;
		
		AfaePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(prefListener);
	}
	
	/*
	 * this allows for the hover over errors in the gutter
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
	    return new AfaeAnnotationHover();
	}
	
	/**
	 * Adapts the behavior of the contained components to the change
	 * encoded in the given event.
	 * 
	 * @param event the event to which to adapt
	 * @since 2.0
	 */
	protected void adaptToPreferenceChange(PropertyChangeEvent event) {
		//AfaePlugin.logDebug("Prefchange: " + event,null,AfaeSourceViewerConfiguration.class);
		//System.err.println("Prefchange: " + event.getProperty());
		
		if(event.getOldValue() instanceof Boolean 
			|| event.getProperty().indexOf(IPreferenceConstants.BACKGROUND_COLOR_SUFFIX) > 0) {
			//adaptToStyleChange(event);
		} else {
			adaptToColorChange(event);
		}
	}

	/** 
	 * Copied from org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration
	 * controls the tab / applekey+tab many line indent function
	 */
	/*public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		List<String> list = new ArrayList<String>();
		// prefix[0] is either '\t' or ' ' x tabWidth, depending on useSpaces
		
		int tabWidth = getPreferenceStore().getInt(IPreferenceConstants.P_EDITOR_TAB_WIDTH);
		boolean useSpaces = getPreferenceStore().getBoolean(IPreferenceConstants.P_EDITOR_SPACES_FOR_TABS);
		
		for(int i= 0; i <= tabWidth; i++) {
		    StringBuffer prefix = new StringBuffer();
			if(useSpaces) {
			    for (int j = 0; j + i < tabWidth; j++)
			    		prefix.append(' ');
		    	
				if (i != 0)
		    			prefix.append('\t');				
			} else {    
			    for (int j = 0; j < i; j++)
			    		prefix.append(' ');
				if (i != tabWidth)
		    			prefix.append('\t');
			}
			list.add(prefix.toString());
		}
		
		list.add(""); //$NON-NLS-1$
		return (String[]) list.toArray(new String[list.size()]);
	}*/

	/**
	 * Gets the tab width from the preference store
	public int getTabWidth() {
		return getPreferenceStore().getInt(IPreferenceConstants.P_EDITOR_TAB_WIDTH);
	} */

	/**
	 * Gets the preference store
	 * @return
	 */
	private IPreferenceStore getPreferenceStore() {
		return AfaePlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Called when the preference pages have an update that needs to get
	 * handled
	 * @param event
	 */
	private void adaptToColorChange(PropertyChangeEvent event) {
		RGB rgb = null;
		Token token = (Token)tokenMap.get(event.getProperty());
		LToken lToken = (LToken)tokenMap.get(event.getProperty() + MARK_SUFFIX);
		
		if(token == null && lToken == null) 
			return;
		
		Object value = event.getNewValue();
		
		if(value instanceof RGB)
			rgb = (RGB)value;
		else if(value instanceof String)
			rgb = StringConverter.asRGB((String) value);
		
		if(rgb != null) {
			String property = event.getProperty();
			Object data = token.getData();
			
			if(data instanceof TextAttribute) {
				TextAttribute oldAttr = (TextAttribute)data;
				
				TextAttribute newAttr = new TextAttribute(
					colorManager.getColor(property), 
					oldAttr.getBackground(),
					oldAttr.getStyle()
				);
				if(token != null) token.setData(newAttr);
				if(lToken != null) lToken.setData(newAttr);
			}
		}
	}
	
	/**
	 * Loops over all the text styles FUNCTION, LABEL1, etc, and sets the
	 * bold, italic, underline, strike, and highlighting background color.
	 */
	public void refreshAllStyles() {
		String[] alltypes = IPreferenceConstants.ALL_TEXT_COLORS;
		
		boolean bold = false;
		boolean italic = false;
		boolean underline = false;
		boolean strike = false;
		String bgcolor = "";
		String colorName = "";
		Color newbgcolor = null;
		RGB rgb = null;
		
		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
		
		for(byte z=0; z<alltypes.length; z++) {
			//get the current base color name
			colorName = alltypes[z];
			//get the tokens
			Token token = (Token)tokenMap.get(colorName);
			LToken lToken = (LToken) tokenMap.get(colorName + MARK_SUFFIX);
			
			bold = store.getBoolean(colorName + IPreferenceConstants.BOLD_SUFFIX);
			italic = store.getBoolean(colorName + IPreferenceConstants.ITALIC_SUFFIX);
			underline = store.getBoolean(colorName + IPreferenceConstants.UNDERLINE_SUFFIX);
			strike = store.getBoolean(colorName + IPreferenceConstants.STRIKE_SUFFIX);
			bgcolor = store.getString(colorName + IPreferenceConstants.BACKGROUND_COLOR_SUFFIX);
			
			//System.err.println("All Types: " + colorName + " ( "+bgcolor+" ) " + bold + " " + italic +  " " + underline + " " + strike);
			
			Object data = token.getData();
			if(data instanceof TextAttribute) {
				int new_style = SWT.NORMAL;
				
				if(!"null".equals(bgcolor) && !"".equals(bgcolor)) {
					try {
						rgb = StringConverter.asRGB(bgcolor);
						newbgcolor = AfaePlugin.getDefault().getColorManager().newColor(rgb);
					} catch (org.eclipse.jface.resource.DataFormatException dfe) {
						//we tried and the color was bunk.
						System.err.println("Bunk background color: " + bgcolor + " for " + colorName);
						dfe.printStackTrace();
					}
				} else {
					newbgcolor = null;
				}
				
				new_style = flipBit(new_style, SWT.BOLD, bold);
				new_style = flipBit(new_style, SWT.ITALIC, italic);
				new_style = flipBit(new_style, TextAttribute.UNDERLINE, underline);
				new_style = flipBit(new_style, TextAttribute.STRIKETHROUGH, strike);
				
				//the original color of the text doesn't seem to have a problem saving
				//so just reuse that
				TextAttribute newAttr = new TextAttribute(
					((TextAttribute)data).getForeground(), 
					newbgcolor, 
					new_style
				);
				
				if(token != null)token.setData(newAttr);
				if(lToken != null)lToken.setData(newAttr);
			}
		}
	}
	
	//used to flip style bits in the SWT stuff
	private int flipBit(int start, int bit, boolean shouldflip){
		if(shouldflip && (start & bit) != bit)
			start ^= bit;
		else if(!shouldflip && (start & bit) == bit)
			start ^= bit;
		return start;
	}
	
	/*
	 * see super
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		
		//do spans and sequences in the normal document
		addAllButKeywordHighlighting(reconciler);
		
		//do everything in the delegate sections
		addDelegatedKeywords(reconciler);
		
		//add the keywords from the normal section
		addKeywordHighlighting(reconciler);
		
		return reconciler;
	}
	
	/**
	 * Handles sub partition types. For example the script section in html
	 * Transforms a mode.rule.keyword to a rulebasedscanner type keyword
	 * @param reconciler
	 */
	private void addDelegatedKeywords(PresentationReconciler reconciler) {
		//get all the delegates out of the mode (javascript::MAIN for example)
		Collection<String> delegates = mode.getDelegates().keySet();
		
		//loop over each delegate and get out the set of jEdit mode rules they have
		for(Iterator<String> rules = delegates.iterator(); rules.hasNext();) {
			String mungedName = (String)rules.next();
			//System.err.println(mungedName);
			
			Rule rule = (Rule)mode.getDelegates().get(mungedName);
			
			RuleBasedScanner scanner = getDelegateScanner(rule);
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			
			reconciler.setDamager(dr, mungedName);
			reconciler.setRepairer(dr, mungedName);
		}
	}

	/**
	 * Creates a rules based scanner based on a jEdit rule set
	 * @param rule
	 * @return
	 */
	private RuleBasedScanner getDelegateScanner(Rule rule) {
		RuleBasedScanner scanner = new RuleBasedScanner();
		List<IRule> rules = new ArrayList<IRule>();
		//get the color / token
		String colorName = colorManager.colorForType(rule.getDefaultTokenType());
		IToken defaultToken = newToken(colorName);
		
		scanner.setDefaultReturnToken(defaultToken);
		
		AfaeEditorTools.add(rule, rules, new ITokenFactory() {
			public IToken makeToken(Type type) {
				String color = colorManager.colorForType(type.getColor());
				return newToken(color);
			}
		});
		
		addTextSequenceRules(rule, rules, defaultToken);
		
		scanner.setRules((IRule[]) rules.toArray(new IRule[rules.size()]));
		return scanner;
	}

	/**
	 * adds the other rules (not keywords) to the reconciler 
	 * @param reconciler
	 */
	private void addAllButKeywordHighlighting(PresentationReconciler reconciler) {
		setupScannerType(reconciler, Type.SINGLE_S);
		setupScannerType(reconciler, Type.MULTI_S);
		setupScannerType(reconciler, Type.SEQ);
		setupScannerType(reconciler, Type.EOL_SPAN);
		//setupScannerType(reconciler, "");
		//setupScannerTypeForMark(reconciler);
	}

	/**
	 * Helper function ...
	 * @param reconciler
	 * @param typeName
	 */
	private void setupScannerType(PresentationReconciler reconciler, String typeName) {
		String[] contentTypes = mode.getContentTypes();
		
		//java.util.HashMap map = (HashMap)mode.getDelegates();
		
		for(int i = 0; i < contentTypes.length; i++) {
			String contentType = contentTypes[i];
			//remove thispart..
			//if(!contentType.startsWith(typeName)) 
			//	continue;
			
			//can be like
			// DELEGATE_S.KEYWORD2 or javascript::MAINDELEGATE_S.MARKUP
			RuleBasedScanner scanner = new RuleBasedScanner();			
			String type = contentType.substring(contentType.lastIndexOf('.') + 1);
			
			//find out if the font should be bold or plain text
			//set the forground color and bold style for the token
			IToken defaultToken = newToken(colorManager.colorForType(type));
						
			//if this looks like a delegate content type...
//			if(contentType.indexOf("::") > 0)
//			{
//				//try to get out the name as it is in the XML file so we can lookup
//				//the rules in the mode
//				//String modeDelegateName = type.replaceAll("DELEGATE_S","~");
//				//String xmlName = modeDelegateName.substring(0,modeDelegateName.lastIndexOf('~'));
//				
//				//now xml Name should be like javascript::MAIN, try to get it from the
//				//modes delegates
//				if(map.containsKey(contentType))
//				{	
//					Rule delmode = (Rule)map.get(contentType);
//					List newrules = new ArrayList();
//					
//					//change the mode rules to eclipse rules
//					AfaeEditorTools.add(delmode, newrules, new ITokenFactory() {
//						public IToken makeToken(Type type) 
//						{
//							String color = colorManager.colorForType(type.getColor());
//							return newToken(color);
//						}
//					});
//					
//					//now add the rules for this content type....
//					scanner.setRules((IRule[]) newrules.toArray(new IRule[newrules.size()]));
//				}
//			}
//			else
//			{
				scanner.setDefaultReturnToken(defaultToken);
//			}
			
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			
			reconciler.setDamager(dr, contentType);
			reconciler.setRepairer(dr, contentType);
		}
	}
	
	/**
	 * 
	 * @param reconciler
	 */
	/*
	private void setupScannerTypeForMark(PresentationReconciler reconciler)	{
	
		String[] contentTypes = mode.getContentTypes();
		
		for(int i = 0; i < contentTypes.length; i++) {
			String contentType = contentTypes[i];
			
			if(contentType.startsWith(Type.MARK_PREVIOUS) || contentType.startsWith(Type.MARK_FOLLOWING)) {
				RuleBasedScanner scanner = new RuleBasedScanner();			
				String colorType = contentType.substring(contentType.lastIndexOf('.') + 1);
				
				LToken defaultToken = (LToken) newToken(colorManager.colorForType(colorType), true);
				
				defaultToken.setLength(getLength(contentType));
				defaultToken.isPrevious(contentType.startsWith(Type.MARK_PREVIOUS));
				
				scanner.setDefaultReturnToken(defaultToken);
				
				//DefaultDamagerRepairer dr = new MarkDamagerRepairer(scanner);
				DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
				reconciler.setDamager(dr, contentType);
				reconciler.setRepairer(dr, contentType);
			}
		}
	} */

	/**
	 * 
	 * @param contentType
	 * @return
	 */
	/*private int getLength(String contentType) {
		int start = contentType.indexOf('@');
		
		if(start == -1) 
			return 0;
		
		int end = contentType.indexOf('.');
		
		if(end == -1) 
			return 0;
		
		try {
			return Integer.parseInt(contentType.substring(start + 1, end));
		}
		catch(NumberFormatException ne)	{
			AfaePlugin.logWarn(
				"getLength for content type: " + contentType,
				ne,
				AfaeSourceViewerConfiguration.class
			);
			return 0;
		}
	}*/

	/**
	 * Adds keyword highlighting... 
	 * @param reconciler
	 */
	private void addKeywordHighlighting(PresentationReconciler reconciler) {
		RuleBasedScanner codeScanner = getCodeScanner();
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(codeScanner);
		
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	}

	/**
	 * 
	 * @return
	 */
	public RuleBasedScanner getCodeScanner() {
		RuleBasedScanner scanner = new RuleBasedScanner();
		List<IRule> rules = new ArrayList<IRule>();
		Rule main = mode.getDefaultRuleSet();
		addWhitespaceRule(rules);

		IToken defaultToken = newToken(ColorsPreferencePage.NULL_COLOR);
		addTextSequenceRules(main, rules, defaultToken);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		scanner.setRules(result);
		return scanner;
	}

	/**
	 * 
	 * @param rules
	 */
	private void addWhitespaceRule(List<IRule> rules) {
		rules.add(new WhitespaceRule(new ColoringWhitespaceDetector()));
	}
	
	/**
	 * 
	 * @param ruleSet
	 * @param rules
	 * @param defaultToken
	 */
	private void addTextSequenceRules(Rule ruleSet, List<IRule> rules, IToken defaultToken) {
		ColoringWordDetector wordDetector = new ColoringWordDetector();
		
		if(ruleSet.getHighlightDigits()) 
			rules.add(new NumberRule(newToken(ColorsPreferencePage.DIGIT_COLOR)));
		
		CasedWordRule wordRule = new CasedWordRule(
			wordDetector, defaultToken, ruleSet.getKeywords().ignoreCase()
		);
		wordRule.setAtLineStart(ruleSet.getKeywords().isAtLineStart());

		addKeywordRule(ruleSet, COMMENT1, ColorsPreferencePage.COMMENT1_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, COMMENT2, ColorsPreferencePage.COMMENT2_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, LITERAL1, ColorsPreferencePage.LITERAL1_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, LITERAL2, ColorsPreferencePage.LITERAL2_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, LABEL, ColorsPreferencePage.LABEL_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, KEYWORD1, ColorsPreferencePage.KEYWORD1_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, KEYWORD2, ColorsPreferencePage.KEYWORD2_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, KEYWORD3, ColorsPreferencePage.KEYWORD3_COLOR, wordRule, wordDetector);		
		addKeywordRule(ruleSet, FUNCTION, ColorsPreferencePage.FUNCTION_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, MARKUP, ColorsPreferencePage.MARKUP_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, OPERATOR, ColorsPreferencePage.OPERATOR_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, DIGIT, ColorsPreferencePage.DIGIT_COLOR, wordRule, wordDetector);
		addKeywordRule(ruleSet, INVALID, ColorsPreferencePage.INVALID_COLOR, wordRule, wordDetector);
		
		rules.add(wordRule);
	}

	/**
	 * 
	 * @param colorName
	 * @return
	 */
	protected IToken newToken(String colorName) {
		return newToken(colorName, false);
	}
	
	/**
	 * Given a token name, gets all the TextAttribute items and makes the correct
	 * Token out of it. So that the text's Token is colored correctly.
	 * @param colorName
	 * @param isMark
	 * @return
	 */
	private IToken newToken(String colorName, boolean isMark) {
		String suffix = isMark ? MARK_SUFFIX : "";
		
		//if we have the token cached, use it
		IToken token = (IToken) tokenMap.get(colorName + suffix);
		if(token != null) {
			return token;
		}
		
		//find out if the font should be bold or plain text
		//set the forground color and bold style for the token
		TextAttribute ta = new TextAttribute(
			colorManager.getColor(colorName), 
			null, 
			colorManager.getStyleFor(colorName)
		);
		//if it's a mark token, create a token with a length
		token = isMark ? new LToken(ta) : new Token(ta);
		
		//tokenMap.clear();
		tokenMap.put(colorName + suffix, token);
		
		return token;
	}
	
	/**
	 * 
	 * @param ruleSet
	 * @param type
	 * @param tokenName
	 * @param keywordRule
	 * @param wordDetector
	 */
	private void addKeywordRule(Rule ruleSet, String type, String tokenName, CasedWordRule keywordRule, ColoringWordDetector wordDetector) {
		String[] keywords = ruleSet.getKeywords().get(type);
		
		IToken keywordToken = null;
		if(tokenMap.containsKey(tokenName)) {
			keywordToken = tokenMap.get(tokenName);
		} else {
			keywordToken = newToken(tokenName);
		}
		
		if(keywords!= null && keywords.length != 0) {
			for (int i = 0; i < keywords.length; i++) {
				wordDetector.addWord(keywords[i]);
				keywordRule.addWord(keywords[i], keywordToken);
			}
		}
		
		List<Type> allOfType = ruleSet.get(type);
		for (Iterator<Type> allI = allOfType.iterator(); allI.hasNext();) {
			Type aType = (Type) allI.next();
			if(aType.getType() == Type.SEQ && 
				wordDetector.isWordStart(((TextSequence)aType).getText().charAt(0))) {
				keywordRule.addWord(aType.getText(), keywordToken);
			}
		}
	}

	/*
	 * 
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return mode.getContentTypes();
	}

	/*
	 * this seems to get called only on initial creation and gets all the indent 
	 * types one after the other
	 * for example: 
	 * getAutoEditStrategies They want a content type of: TAGSDELEGATE_S.MARKUP
	 * getAutoEditStrategies They want a content type of: MULTI_S.COMMENT1
	 * getAutoEditStrategies They want a content type of: MULTI_S.LABEL
	 * getAutoEditStrategies They want a content type of: DELEGATE_S.KEYWORD2
	 * ...
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		AfaePlugin.logDebug("getAutoEditStrategies They want a content type of: " +contentType,null,this.getClass());
		
		//TODO: we'll see how well this works :) the KEYWORD2 is used in xslt as the
		//main tag painting, sadly it's also used for #imports <asdf> in C style 
		//languages.
		if(contentType.indexOf("MARKUP") > 0 || contentType.indexOf("KEYWORD2") > 0) {
			return new IAutoEditStrategy[] { chevronIndentStrategy };
		} else {
			return new IAutoEditStrategy[] { bracketIndentStrategy };
		}
	}

	/**
	 * Gets the mode this viewer configuration is set to
	 * @return
	 */
	public Mode getMode() {
		return mode;
	}

	/** 
	 * Inform the SourceViewerConfiguration of the filename of
	 * the editor. This information is needed so the receiver
	 * can setup it's mode.
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		setMode(ModeCache.getModeFor(filename));
	}
	
	/**
	 * Sets the mode for the file
	 * @param theMode
	 */
	private void setMode(Mode theMode) {
		mode = theMode;
	}
}