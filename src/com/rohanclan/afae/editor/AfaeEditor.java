/*
 * Copyright (c) 2005 Rob Rohan 
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

import java.net.URI;
import java.util.Iterator; //import java.util.LinkedHashSet;
import java.util.LinkedList; //import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter; //import org.eclipse.jface.text.BadLocationException;
//import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.util.PropertyChangeEvent; //import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText; //import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseMoveListener;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display; //import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.IStatusField;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.IImageConstants;
import com.rohanclan.afae.decoration.DecorationSupport;
import com.rohanclan.afae.editor.pairs.AfaePairMatcher;
import com.rohanclan.afae.editor.pairs.Pair;
import com.rohanclan.afae.modes.IModeConstants;
import com.rohanclan.afae.modes.Mode; //import com.rohanclan.afae.outline.DefaultOutlinePage;
//import com.rohanclan.afae.outline.IAfaeOutlinePage;
import com.rohanclan.afae.outline.AfaeContentOutlinePage;
import com.rohanclan.afae.prefs.IPreferenceConstants;
import com.rohanclan.afae.rules.ColorManager;

/**
 * The editor editor
 * 
 * @author robrohan AbstractDecoratedTextEditor
 */
public class AfaeEditor extends TextEditor implements IPreferenceConstants,
		IModeConstants {
	// private TabConverter tabConverter;
	protected LineNumberRulerColumn fLineNumberRulerColumn;
	// private IChangeRulerColumn fChangeRulerColumn;
	protected AfaeContentOutlinePage page;

	/**
	 * The constructor.
	 */
	public AfaeEditor() {
		super();

		setDocumentProvider(new AfaeDocumentProvider());
		ColorManager colorManager = AfaePlugin.getDefault().getColorManager();

		// setup the source viewer which does a lot of things, indentation being
		// one of them
		setSourceViewerConfiguration(new AfaeSourceViewerConfiguration(
				colorManager, this));

		/*
		 * This is needed for the editor to respond to preference changes from
		 * the AfaePreferencePage. When the Workbench's preferences change we
		 * want to update too, if they are font changes.
		 */
		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	/**
	 * gets the source viewer for this editor (often use to get the text widget
	 * out)
	 * 
	 * @return
	 */
	public ISourceViewer sourceViewer() {
		return this.getSourceViewer();
	}

	/*
	 * this is here for when I get around to doing the ftp view plugin. This is
	 * needed it seems
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);

		if (input instanceof IPathEditorInput) {
			String filename = ((IPathEditorInput) input).getName();
			((AfaeSourceViewerConfiguration) getSourceViewerConfiguration())
					.setFilename(filename);
		} else if (input instanceof ILocationProvider) {
			IPath path = ((ILocationProvider) input).getPath(input);
			((AfaeSourceViewerConfiguration) getSourceViewerConfiguration())
					.setFilename(path.toString());
		} else if (input instanceof IStorageEditorInput) {
			String filename = ((IStorageEditorInput) input).getStorage()
					.getName();
			((AfaeSourceViewerConfiguration) getSourceViewerConfiguration())
					.setFilename(filename);
		} else if (input instanceof FileStoreEditorInput) {
			URI fileuri = ((FileStoreEditorInput)input).getURI();
			String filename = new Path(fileuri.getPath()).toOSString();
			((AfaeSourceViewerConfiguration) getSourceViewerConfiguration())
					.setFilename(filename);
		}
	}

	/*
	 * called right before the right click menu appears {@inheritDoc}
	 */
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		// super.editorContextMenuAboutToShow(menu);
		menu.add(new Separator(ITextEditorActionConstants.GROUP_UNDO));
		menu.add(new GroupMarker(ITextEditorActionConstants.GROUP_SAVE));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_COPY));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_PRINT));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_EDIT));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_FIND));
		menu.add(new Separator(IWorkbenchActionConstants.GROUP_ADD));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		if (isEditable()) {
			addAction(menu, ITextEditorActionConstants.GROUP_UNDO,
					ITextEditorActionConstants.UNDO);
			addAction(menu, ITextEditorActionConstants.GROUP_UNDO,
					ITextEditorActionConstants.REVERT_TO_SAVED);
			addAction(menu, ITextEditorActionConstants.GROUP_SAVE,
					ITextEditorActionConstants.SAVE);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY,
					ITextEditorActionConstants.CUT);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY,
					ITextEditorActionConstants.COPY);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY,
					ITextEditorActionConstants.PASTE);
		} else {
			addAction(menu, ITextEditorActionConstants.GROUP_COPY,
					ITextEditorActionConstants.COPY);
		}

		menu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS,
				new Separator(ITextEditorActionConstants.GROUP_SETTINGS));
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT,
				ITextEditorActionConstants.SHIFT_RIGHT);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT,
				ITextEditorActionConstants.SHIFT_LEFT);

		addSpecificMenuItems(menu);
	}

	/**
	 * add in our custom actions
	 * 
	 * @param menu
	 */
	protected void addSpecificMenuItems(IMenuManager menu) {
		try {
			menu.appendToGroup("additions",
					getAction(ITextEditorActionConstants.FIND));
			menu.appendToGroup("additions",
					getAction(ITextEditorActionConstants.FIND_NEXT));
			menu.appendToGroup("additions",
					getAction(ITextEditorActionConstants.FIND_PREVIOUS));

			menu.add(getAction(ITextEditorActionConstants.GOTO_LINE));

			menu.add(getAction(ITextEditorActionConstants.UPPER_CASE));
			menu.add(getAction(ITextEditorActionConstants.LOWER_CASE));
		} catch (Exception e) {
			AfaePlugin.logError("addSpecificMenuItems", e, AfaeEditor.class);
		}
	}

	/*
	 * @see IAdaptable#getAdapter(java.lang.Class)
	 * 
	 * @since 2.0 this is called when the outline is requested (among other
	 * things)
	 */
	/**
	 * (non-Javadoc) Method declared on IAdaptable
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class key) {
		
		if (key.equals(IContentOutlinePage.class)) {
			IEditorInput input = getEditorInput();
			
			if (input instanceof IFileEditorInput) {
				page = new AfaeContentOutlinePage(((IFileEditorInput) input).getFile());
				return page;
			}
		}
		return super.getAdapter(key);
	}

	/*
	 * public Object getAdapter(Class required) {
	 * AfaePlugin.logDebug("got a request for: " +
	 * required.toString(),null,AfaeEditor.class);
	 * 
	 * // if they ask for the outline page send our implementation if
	 * (required.getName
	 * ().trim().equals("org.eclipse.ui.views.contentoutline.IContentOutlinePage"
	 * )) { try { IAfaeOutlinePage aop = AfaePlugin.getOutliner(".js");
	 * 
	 * if (aop != null) { AfaePlugin.logDebug("loading outliner", null,
	 * AfaeEditor.class); aop.initOutlineToEditor(this); aop.update();
	 * 
	 * // // final IAfaeOutlinePage faop = aop; //
	 * this.getDocumentProvider().getDocument(null).addDocumentListener( // new
	 * IDocumentListener() { public void //
	 * documentAboutToBeChanged(DocumentEvent event) {;} public // void
	 * documentChanged(DocumentEvent event) { // faop.update(); } } ); //
	 * 
	 * AfaePlugin.logDebug("done", null, AfaeEditor.class); } else { aop = new
	 * DefaultOutlinePage(); aop.initOutlineToEditor(this); }
	 * 
	 * AfaePlugin.logDebug("returning: " + aop, null, AfaeEditor.class); return
	 * aop; } catch (Exception e) { AfaePlugin.logError("getAdapter", e,
	 * AfaeEditor.class); }
	 * 
	 * return super.getAdapter(required); }
	 * 
	 * return super.getAdapter(required); }
	 */

	/**
	 * This is mostly used to get access to the underlying Mode of this editor.
	 * There is other information in the ViewerConfiguration as well however
	 * 
	 * @return
	 */
	public AfaeSourceViewerConfiguration getSourceViewerConfig() {
		return (AfaeSourceViewerConfiguration) getSourceViewerConfiguration();
	}

	/**
	 * Shows a little message at the bottom of the editor to show what mode you
	 * are in (also shows a little afae head)
	 */
	protected void updateStatusField(String category) {
		if (category == null)
			return;

		if (category.equals(AfaePlugin.STATUS_CATEGORY_MODE)) {
			IStatusField field = getStatusField(category);

			if (field != null) {
				field.setImage(AfaePlugin.getImage(IImageConstants.IMG_AFAE16));

				Mode mode = ((AfaeSourceViewerConfiguration) getSourceViewerConfiguration())
						.getMode();
				String text = mode == null ? "No mode" : mode.getDisplayName();
				field.setText(text);
				return;
			}
		}

		super.updateStatusField(category);
	}

	/*
	 * 
	 */
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return AfaePlugin.getDefault().getEditorTools()
				.affectsTextPresentation(event);
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		setBackgroundColor();
		setWordWrap();
	}

	/**
	 * Set the background color of the editor window based on the user's
	 * preferences
	 */
	private void setBackgroundColor() {
		// Only try to set the background color when the source fViewer is
		// available
		if (this.getSourceViewer() != null
				&& this.getSourceViewer().getTextWidget() != null) {
			IPreferenceStore store = AfaePlugin.getDefault()
					.getPreferenceStore();

			this.getSourceViewer().getTextWidget()
					.setForeground(
							new org.eclipse.swt.graphics.Color(Display
									.getCurrent(), StringConverter.asRGB(store
									.getString(NULL_COLOR))));

			this.getSourceViewer().getTextWidget().setBackground(
					new org.eclipse.swt.graphics.Color(Display.getCurrent(),
							StringConverter.asRGB(store
									.getString(INVALID_COLOR))));

			// //// The newer style stuff needs to repartition the whole
			// document when applied
			// //// it can't just update the single attributes (to handle
			// background changes and
			// //// other items. The following updates the background and style
			// settings for all
			// //// of text elements IPreferencesConstants.ALL_TEXT_STYLES
			((AfaeSourceViewerConfiguration) this
					.getSourceViewerConfiguration()).refreshAllStyles();
			Object ele = ((AfaeDocumentProvider) this.getDocumentProvider()).element;
			((AfaeDocumentProvider) this.getDocumentProvider())
					.partitionDocument(this.getDocumentProvider().getDocument(
							ele), ele);
			// //////////////
		}
	}

	/**
	 * Turns on the word wrap if they have the preference set (mostly used when
	 * opening a new document or changing the setting
	 */
	private void setWordWrap() {
		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();
		StyledText st = this.sourceViewer().getTextWidget();
		boolean textmode_on = store.getBoolean(P_USE_WORD_WRAP);
		st.setWordWrap(textmode_on);
	}

	/**
	 * 
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		try {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null)
				return;

			super.handlePreferenceStoreChanged(event);

			String property = event.getProperty();
			// IPreferenceStore store =
			// AfaePlugin.getDefault().getPreferenceStore();

			if (P_USE_WORD_WRAP.equals(property)) {
				this.setWordWrap();
			}

			// TODO: I don't like this, but this sets the background after every
			// preference
			// change. If I don't let it do that, it messes up the background
			// color sometimes
			// need to figure out why as this is a total waste of cycles.
			setBackgroundColor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This method configures the editor but does not define a
	 * <code>SourceViewerConfiguration</code>. When only interested in providing
	 * a custom source viewer configuration, subclasses may extend this method.
	 */
	protected void initializeEditor() {
		// AfaePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		// org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants

		IPreferenceStore store = AfaePlugin.getDefault().getPreferenceStore();

		store.setDefault(P_SHOW_LINE_NUMBERS, true);
		store.setDefault(P_SHOW_PRINT_MARGIN, false);
		store.setDefault(P_PRINT_MARGIN_SIZE, 80);

		store.setDefault(P_SHOW_OVERVIEW_RULER, true);
		store.setDefault(P_HIGHLIGHT_CURRENT_LINE, true);
		store.setDefault(P_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
		store.setDefault(P_SELECTION_BACKGROUND_SYSTEM_DEFAULT, false);
		store.setDefault(P_TAB_WIDTH, 4);
		store.setDefault(P_BRACKET_MATCHING_ENABLED, true);
		store.setDefault(P_USE_WORD_WRAP, false);

		/*
		 * TextThemes tt = new TextThemes(); try{ tt.loadTheme("Default");
		 * tt.applyTheme(); }catch(IOException e){ System.err.println(e); }
		 */
		store.setDefault(P_LINE_NUMBER_COLOR, "0,0,0");
		store.setDefault(P_CURRENT_LINE_COLOR, "232,242,254");
		store.setDefault(P_PRINT_MARGIN_COLOR, "176,180,185");
		store.setDefault(P_SELECTION_BACKGROUND_COLOR, "198,208,219");
		store.setDefault(P_BRACKET_MATCHING_COLOR, "104,104,104");

		// set the default colors
		store.setDefault(NULL_COLOR, "0,0,0");

		store.setDefault(KEYWORD1_COLOR, "103,1,85");
		store.setDefault(KEYWORD1_COLOR + BOLD_SUFFIX, true);
		store.setDefault(KEYWORD1_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(KEYWORD1_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(KEYWORD1_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(KEYWORD1_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(KEYWORD2_COLOR, "103,1,85");
		store.setDefault(KEYWORD2_COLOR + BOLD_SUFFIX, true);
		store.setDefault(KEYWORD2_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(KEYWORD2_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(KEYWORD2_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(KEYWORD2_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(KEYWORD3_COLOR, "103,1,85");
		store.setDefault(KEYWORD3_COLOR + BOLD_SUFFIX, true);
		store.setDefault(KEYWORD3_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(KEYWORD3_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(KEYWORD3_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(KEYWORD3_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(COMMENT1_COLOR, "1,94,4");
		store.setDefault(COMMENT1_COLOR + BOLD_SUFFIX, false);
		store.setDefault(COMMENT1_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(COMMENT1_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(COMMENT1_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(COMMENT1_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(COMMENT2_COLOR, "1,94,4");
		store.setDefault(COMMENT2_COLOR + BOLD_SUFFIX, false);
		store.setDefault(COMMENT2_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(COMMENT2_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(COMMENT2_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(COMMENT2_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(LITERAL1_COLOR, "0,21,187");
		store.setDefault(LITERAL1_COLOR + BOLD_SUFFIX, false);
		store.setDefault(LITERAL1_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(LITERAL1_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(LITERAL1_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(LITERAL1_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(LITERAL2_COLOR, "47,59,59");
		store.setDefault(LITERAL2_COLOR + BOLD_SUFFIX, true);
		store.setDefault(LITERAL2_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(LITERAL2_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(LITERAL2_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(LITERAL2_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(LABEL_COLOR, "91,91,91");
		store.setDefault(LABEL_COLOR + BOLD_SUFFIX, false);
		store.setDefault(LABEL_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(LABEL_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(LABEL_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(LABEL_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(FUNCTION_COLOR, "0,0,0");
		store.setDefault(FUNCTION_COLOR + BOLD_SUFFIX, false);
		store.setDefault(FUNCTION_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(FUNCTION_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(FUNCTION_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(FUNCTION_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(MARKUP_COLOR, "18,0,185");
		store.setDefault(MARKUP_COLOR + BOLD_SUFFIX, true);
		store.setDefault(MARKUP_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(MARKUP_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(MARKUP_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(MARKUP_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(OPERATOR_COLOR, "0,0,0");
		store.setDefault(OPERATOR_COLOR + BOLD_SUFFIX, false);
		store.setDefault(OPERATOR_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(OPERATOR_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(OPERATOR_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(OPERATOR_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(DIGIT_COLOR, "186,0,0");
		store.setDefault(DIGIT_COLOR + BOLD_SUFFIX, false);
		store.setDefault(DIGIT_COLOR + ITALIC_SUFFIX, false);
		store.setDefault(DIGIT_COLOR + UNDERLINE_SUFFIX, false);
		store.setDefault(DIGIT_COLOR + STRIKE_SUFFIX, false);
		store.setDefault(DIGIT_COLOR + BACKGROUND_COLOR_SUFFIX, "null");

		store.setDefault(INVALID_COLOR, "255,255,255");
		/*
		 * store.setDefault(INVALID_COLOR + BOLD_SUFFIX, false);
		 * store.setDefault(INVALID_COLOR + ITALIC_SUFFIX, false);
		 * store.setDefault(INVALID_COLOR + UNDERLINE_SUFFIX, false);
		 * store.setDefault(INVALID_COLOR + STRIKE_SUFFIX, false);
		 * store.setDefault(INVALID_COLOR + BACKGROUND_COLOR_SUFFIX, "null");
		 */

		setRulerContextMenuId("#TextRulerContext"); //$NON-NLS-1$
		setHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
		setPreferenceStore(AfaePlugin.getDefault().getPreferenceStore());

		configureInsertMode(SMART_INSERT, false);
		setInsertMode(INSERT);
	}

	/*
	 * 
	 */
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		AfaeSourceViewer viewer = new AfaeSourceViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);

		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	/**
	 * Returns the source viewer decoration support.
	 * 
	 * @param viewer
	 *            the viewer for which to return a decoration support
	 * @return the source viewer decoration support
	 */
	protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(
			ISourceViewer viewer) {
		if (fSourceViewerDecorationSupport == null) {
			fSourceViewerDecorationSupport = new DecorationSupport(viewer,
					getOverviewRuler(), getAnnotationAccess(),
					getSharedColors());
			configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);
		}
		return fSourceViewerDecorationSupport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.texteditor.AbstractDecoratedTextEditor#
	 * configureSourceViewerDecorationSupport
	 * (org.eclipse.ui.texteditor.SourceViewerDecorationSupport)
	 */
	protected void configureSourceViewerDecorationSupport(
			SourceViewerDecorationSupport support) {
		AfaePairMatcher bracketMatcher;

		// this is for bracket matching
		// create the pairs for testing
		Pair parenthesis = new Pair("(", ")", 1);
		Pair curlyBraces = new Pair("{", "}", 1);
		Pair squareBraces = new Pair("[", "]", 1);

		// create the collection
		LinkedList<Pair> brackets = new LinkedList<Pair>();
		brackets.add(parenthesis);
		brackets.add(curlyBraces);
		brackets.add(squareBraces);
		// brackets.add(new Pair("def", "end", 3));

		// brackets.add(new Pair("<", ">", 1));
		// brackets.add(new Pair("\"", "\"", 1));
		// brackets.add(new Pair("'", "'", 1));

		// create the CFMLPairMatcher
		bracketMatcher = new AfaePairMatcher(brackets);

		// register the pair matcher
		support.setCharacterPairMatcher(bracketMatcher);

		// register the brackets and colors
		support.setMatchingCharacterPainterPreferenceKeys(
				P_BRACKET_MATCHING_ENABLED, P_BRACKET_MATCHING_COLOR);

		super.configureSourceViewerDecorationSupport(support);

		@SuppressWarnings( { "unchecked" })
		Iterator e = getAnnotationPreferences().getAnnotationPreferences()
				.iterator();

		while (e.hasNext()) {
			AnnotationPreference pref = (AnnotationPreference) e.next();

			support.setAnnotationPainterPreferenceKeys(
					pref.getAnnotationType(), pref.getColorPreferenceKey(),
					pref.getTextPreferenceKey(), pref
							.getOverviewRulerPreferenceKey(), 4);
		}
		support.install(this.getPreferenceStore());
	}

	public void dispose() {
		super.dispose();
	}
}
