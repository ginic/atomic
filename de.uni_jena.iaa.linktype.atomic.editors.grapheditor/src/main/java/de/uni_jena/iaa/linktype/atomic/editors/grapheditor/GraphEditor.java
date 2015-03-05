/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.corpus.GraphService;
import de.uni_jena.iaa.linktype.atomic.core.editors.AtomicGraphicalEditor;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.AtomicEditPartFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.GraphEditorPaletteFactory;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.SpanPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.StructurePart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.TokenPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AdHocSentenceDetectionWizard;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicGraphicalViewerKeyHandler;

/**
 * @author Stephan Druskat
 * 
 */
public class GraphEditor extends AtomicGraphicalEditor {

	IPartListener2 partListener = new IPartListener2() {

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
			try {
				PlatformUI.getWorkbench().showPerspective("de.uni_jena.iaa.linktype.atomic.editors.grapheditor.perspective", PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			}
			catch (WorkbenchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			if (partRef.getPart(false).getClass() == GraphEditor.class) {
				try {
					getSite().getWorkbenchWindow().getWorkbench().showPerspective("de.uni_jena.iaa.linktype.atomic.core.perspective", getSite().getWorkbenchWindow());
				}
				catch (WorkbenchException e) {
					e.printStackTrace();
				}
			}

		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}
	};

	ISelectionListener listener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection incomingSelection) {
			if (!(incomingSelection instanceof IStructuredSelection)) {
				return;
			}
			IStructuredSelection selection = (IStructuredSelection) incomingSelection;
			for (Object element : selection.toList()) {
				// Check if we need to perform an operation at all, i.e.
				// if the selection is interesting
				if (!(element instanceof SSpan || element instanceof SLayer || (element instanceof String && (element.equals(ModelRegistry.NO_LAYERS_SELECTED) || element.equals(ModelRegistry.NO_SENTENCES_SELECTED))))) {
					return;
				}
			}
			boolean containsOnlySpans = true;
			boolean containsOnlyLayers = true;
			for (Object element : selection.toList()) {
				if (!(element instanceof SSpan)) {
					containsOnlySpans = false;
					break;
				}
			}
			for (Object element : selection.toList()) {
				if (!(element instanceof SLayer)) {
					containsOnlyLayers = false;
					break;
				}
			}
			GraphPart graphPart = ((GraphPart) getGraphicalViewer().getRootEditPart().getContents());
			if (selection.isEmpty()) {
				return;
			}
			else if (selection.getFirstElement().equals(ModelRegistry.NO_LAYERS_SELECTED)) {
				graphPart.getLayers().clear();
				getGraphicalViewer().getRootEditPart().getContents().refresh();
			}
			else if (selection.getFirstElement().equals(ModelRegistry.NO_LAYERS_SELECTED)) {
				graphPart.getSortedTokens().clear();
				getGraphicalViewer().getRootEditPart().getContents().refresh();
			}
			else if (containsOnlySpans) {
				graphPart.getSortedTokens().clear();
				graphPart.setSortedTokens(GraphService.getOrderedTokensForSentenceSpans(selection.toList()));
				graphPart.refresh();
				for (Object child : graphPart.getChildren()) {
					if (child instanceof TokenPart || child instanceof SpanPart || child instanceof StructurePart) {
						((AbstractGraphicalEditPart) child).refresh();
					}
				}
			}
			else if (containsOnlyLayers) {
				graphPart.getLayers().clear();
				graphPart.setLayers(new HashSet<SLayer>(selection.toList()));
				graphPart.refresh();
				for (Object child : graphPart.getChildren()) {
					if (child instanceof TokenPart || child instanceof SpanPart || child instanceof StructurePart) {
						((AbstractGraphicalEditPart) child).refresh();
					}
				}
			}
		}
	};
	private GraphicalViewer viewer;

	/**
	 * 
	 */
	public GraphEditor() {
		setEditDomain(new DefaultEditDomain(this));
		getPalettePreferences().setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().getPage().addSelectionListener(listener);
		getSite().getPage().addPartListener(partListener);
		// try {
		// PlatformUI.getWorkbench().showPerspective("de.uni_jena.iaa.linktype.atomic.editors.grapheditor.perspective",
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		// }
		// catch (WorkbenchException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		setViewer(getGraphicalViewer());
		getViewer().setEditPartFactory(new AtomicEditPartFactory());
		getViewer().setRootEditPart(new ScalableFreeformRootEditPart());
		getViewer().setKeyHandler(new AtomicGraphicalViewerKeyHandler(getViewer()));
		getGraphicalViewer().addDropTargetListener(new TemplateTransferDropTargetListener(getGraphicalViewer()));
		getEditDomain().getPaletteViewer().addDragSourceListener(new TemplateTransferDragSourceListener(getEditDomain().getPaletteViewer()));
		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getViewer().getRootEditPart();
		ZoomManager zoomManager = root.getZoomManager();
		root.getZoomManager().setZoomLevels(new double[] { 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 3.0, 4.0 });
		List<String> zoomContributions = Arrays.asList(new String[] { ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH });
		zoomManager.setZoomLevelContributions(zoomContributions);
		zoomManager.setZoomAnimationStyle(ZoomManager.ANIMATE_ZOOM_IN_OUT);
		IAction zoomIn = new ZoomInAction(zoomManager);
		IAction zoomOut = new ZoomOutAction(zoomManager);
		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == ZoomManager.class)
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		return super.getAdapter(type);
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		SLayer sentenceLayer = getGraph().getSLayer(ModelRegistry.SENTENCE_LAYER_SID);
		if (sentenceLayer == null || sentenceLayer.getNodes().isEmpty()) {
			WizardDialog adHocSentenceDetectionsWizard = new WizardDialog(Display.getCurrent().getActiveShell(), new AdHocSentenceDetectionWizard(getGraph()));
			adHocSentenceDetectionsWizard.open();
			// Save document in case layers have changed
			doSave(null);
			// Refresh all tokens (notify them them so they will refresh
			// themselves,
			// as newly added sentence spans' relations will otherwise point
			// into nirvana.
			for (SToken token : getGraph().getSTokens()) {
				token.eNotify(new NotificationImpl(Notification.SET, false, true));
			}
		}
		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart();
		ConnectionLayer connLayer = (ConnectionLayer) root.getLayer(LayerConstants.CONNECTION_LAYER);
		GraphicalEditPart contentEditPart = (GraphicalEditPart) root.getContents();
		FanRouter fanRouter = new FanRouter();
		fanRouter.setSeparation(30);
		AutomaticRouter router = fanRouter;
		ShortestPathConnectionRouter shortestPathConnectionRouter = new ShortestPathConnectionRouter(contentEditPart.getFigure());
		shortestPathConnectionRouter.setSpacing(15);
		router.setNextRouter(shortestPathConnectionRouter);
		connLayer.setConnectionRouter(router);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot
	 * ()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		return GraphEditorPaletteFactory.createPalette();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util
	 * .EventObject)
	 */
	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	public EditPartViewer getEditPartViewer() {
		return getGraphicalViewer();
	}

	public DefaultEditDomain getDomain() {
		return getEditDomain();
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removeSelectionListener(listener);
		getSite().getPage().removePartListener(partListener);
	}

	/**
	 * @return the viewer
	 */
	public GraphicalViewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 *            the viewer to set
	 */
	public void setViewer(GraphicalViewer viewer) {
		this.viewer = viewer;
	}

}
