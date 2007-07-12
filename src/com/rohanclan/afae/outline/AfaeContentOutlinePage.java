package com.rohanclan.afae.outline;

//import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
//import org.eclipse.swt.dnd.DND;
//import org.eclipse.swt.dnd.TextTransfer;
//import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
//import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
//import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.rohanclan.afae.AfaePlugin;

public class AfaeContentOutlinePage extends ContentOutlinePage {
	protected IFile input;

	/**
	 * Creates a new ReadmeContentOutlinePage.
	 */
	public AfaeContentOutlinePage(IFile input) {
		super();
		this.input = input;
		AfaePlugin.logDebug("Loading Outliner", null, AfaeContentOutlinePage.class);
	}

	class OutlineAction extends Action {
		private Shell shell;

		public OutlineAction(String label) {
			super(label);
			getTreeViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						setEnabled(!event.getSelection().isEmpty());
					}
				}
			);
		}

		public void setShell(Shell shell) {
			this.shell = shell;
		}

		public void run() {
			MessageDialog.openInformation(shell, "Readme_Outline", //$NON-NLS-1$
					"ReadmeOutlineActionExecuted"); //$NON-NLS-1$
		}
	}

	/**
	 * Creates the control and registers the popup menu for this page Menu id
	 * "org.eclipse.ui.examples.readmetool.outline"
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);

		/*
		 * PlatformUI.getWorkbench().getHelpSystem().setHelp( getControl(),
		 * IReadmeConstants.CONTENT_OUTLINE_PAGE_CONTEXT );
		 */
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(getContentOutline(input));
		initDragAndDrop();

		// Configure the context menu.
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end")); //$NON-NLS-1$

		Menu menu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		// Be sure to register it so that other plug-ins can add actions.
		getSite().registerContextMenu(
			"org.eclipse.ui.examples.readmetool.outline", 
			menuMgr, 
			viewer
		); //$NON-NLS-1$

		/*
		 * getSite().getActionBars().setGlobalActionHandler(
		 * IReadmeConstants.RETARGET2, new
		 * OutlineAction(MessageUtil.getString("Outline_Action2")));
		 * //$NON-NLS-1$
		 * 
		 * OutlineAction action = new OutlineAction(MessageUtil
		 * .getString("Outline_Action3")); //$NON-NLS-1$
		 * action.setToolTipText(MessageUtil.getString("Readme_Outline_Action3"));
		 * //$NON-NLS-1$ getSite().getActionBars().setGlobalActionHandler(
		 * IReadmeConstants.LABELRETARGET3, action); action = new
		 * OutlineAction(MessageUtil.getString("Outline_Action4"));
		 * //$NON-NLS-1$ getSite().getActionBars().setGlobalActionHandler(
		 * IReadmeConstants.ACTION_SET_RETARGET4, action); action = new
		 * OutlineAction(MessageUtil.getString("Outline_Action5"));
		 * //$NON-NLS-1$
		 * action.setToolTipText(MessageUtil.getString("Readme_Outline_Action5"));
		 * //$NON-NLS-1$ getSite().getActionBars().setGlobalActionHandler(
		 * IReadmeConstants.ACTION_SET_LABELRETARGET5, action);
		 */
	}

	/**
	 * Gets the content outline for a given input element. Returns the outline
	 * (a list of MarkElements), or null if the outline could not be generated.
	 */
	private IAdaptable getContentOutline(IAdaptable input) {
		//////////////////////////////////////////////////////////////////
		/*
		// return ReadmeModelFactory.getInstance().getContentOutline(input);
		
		//Hashtable markTable = new Hashtable(40);
        Vector topLevel = new Vector();
		
		//MarkElement me = new MarkElement(parent, markName, start, end - start);
		int start = 1;
		int end = 10;
		
		MarkElement me = new MarkElement(input, "test", start, end - start);
		topLevel.add(me);
		me = new MarkElement(me, "test2", start+1, (end - start)+1);
		//topLevel.add(me);
		me = new MarkElement(me, "test3", start+2, (end - start)+2);
		//topLevel.add(me);
		
		MarkElement[] results = new MarkElement[topLevel.size()];
	    topLevel.copyInto(results);
	    //results = (MarkElement[])topLevel.toArray();
	    
	    return new AdaptableList(results);
	    */
		return null;
	    ///////////////////////////////////////////////////////////////////
	}

	/**
	 * Initializes drag and drop for this content outline page.
	 */
	private void initDragAndDrop() {
		/*
		 * int ops = DND.DROP_COPY | DND.DROP_MOVE; Transfer[] transfers = new
		 * Transfer[] { TextTransfer.getInstance(), PluginTransfer.getInstance() };
		 * getTreeViewer().addDragSupport(ops, transfers, new
		 * ReadmeContentOutlineDragListener(this));
		 */
	}

	/**
	 * Forces the page to update its contents.
	 * 
	 * @see ReadmeEditor#doSave(IProgressMonitor)
	 */
	public void update() {
		getControl().setRedraw(false);
		getTreeViewer().setInput(getContentOutline(input));
		getTreeViewer().expandAll();
		getControl().setRedraw(true);
	}
}
