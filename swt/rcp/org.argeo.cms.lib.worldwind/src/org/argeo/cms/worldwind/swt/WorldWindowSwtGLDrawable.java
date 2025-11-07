package org.argeo.cms.worldwind.swt;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.swt.GLCanvas;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.WorldWindowGLAutoDrawable;
import gov.nasa.worldwind.WorldWindowImpl;
import gov.nasa.worldwind.event.NoOpInputHandler;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.layers.Earth.BMNGOneImage;
import gov.nasa.worldwind.layers.Earth.UTMGraticuleLayer;
import gov.nasa.worldwind.render.ScreenCreditController;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

/**
 * {@link WorldWindow} based on a pure SWT JogAmp GLCanvas. Not usable
 * interactively at this stage because WorldWind inputs depend on AWT.
 */
public class WorldWindowSwtGLDrawable extends WorldWindowGLAutoDrawable {

	private final GLCanvas canvas;

	public WorldWindowSwtGLDrawable(Composite parent, int style) {
		this(parent, style, new BasicModel(new Earth(), new LayerList()));
	}

	public WorldWindowSwtGLDrawable(Composite parent, int style, Model model) {
		this.canvas = new GLCanvas(parent, style, null, null);

		initDrawable(canvas);
		addPropertyChangeListener(this);
		initGpuResourceCache(WorldWindowImpl.createGpuResourceCache());
		// createView();
		setView(new BasicOrbitView());
		getView().getViewInputHandler().setWorldWindow(this);
		// createDefaultInputHandler();
		setInputHandler(new NoOpInputHandler());
		addPropertyChangeListener(WorldWind.SHUTDOWN_EVENT, this);
		endInitialization();

		setModel(model);

		if (getView() instanceof BasicOrbitView orbitView) {
			// needed so that ExampleUtil.goTo(wwd, modelSector) works
			orbitView.setGlobe(model.getGlobe());
		}

	}

	@Override
	public void redraw() {
		if (this.canvas != null)
			canvas.getDisplay().asyncExec(() -> canvas.redraw());
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
		// This is apparently necessary to enable the WWJ canvas to resize correctly
		// with JSplitPane.
		// ((Component) glAutoDrawable).setMinimumSize(new Dimension(0, 0));
	}

	@Override
	protected void initializeCreditsController() {
		new ScreenCreditController(this);
	}

	@Override
	public void endInitialization() {
		initializeCreditsController();
		// this.dashboard = new DashboardController(this, (Component) this.drawable);
	}

	public static void main(String[] args) throws Exception {
		SurfaceImageLayer imageLayer = new SurfaceImageLayer();
		imageLayer.addImage("/srv/gis/europe/cyprus/data/cyprus-satellite-WGS84.png");
		Layer[] layers = new Layer[] { //
				imageLayer, //
				new BMNGOneImage(), //
				new UTMGraticuleLayer(), //
		};

		Model model = new BasicModel();
		Globe globe = new Earth();
		model.setGlobe(globe);
		model.setLayers(new LayerList(layers));

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("WorldWind SWT (JOGL GLCanvas)");
		shell.setLayout(new FillLayout());

		WorldWindowSwtGLDrawable wwp = new WorldWindowSwtGLDrawable(shell, 0);
		wwp.setView(new BasicOrbitView());
		wwp.setModel(model);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
