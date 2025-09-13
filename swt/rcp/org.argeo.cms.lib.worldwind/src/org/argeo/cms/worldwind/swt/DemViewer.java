package org.argeo.cms.worldwind.swt;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.argeo.cms.worldwind.WorldWindUtils;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;
import gov.nasa.worldwind.layers.Earth.UTMGraticuleLayer;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.terrain.CompoundElevationModel;
import gov.nasa.worldwind.terrain.LocalElevationModel;

/** Simple test application visualizing a digital elevation model. */
public class DemViewer {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("WorldWind SWT");
		shell.setLayout(new FillLayout());

		final WorldWindow ww;
		ww = new WorldWindowSwtAwtCanvas(shell, 0);
		// ww = new WorldWindowSwtGLDrawable(shell, 0);

		new Thread(() -> {
			try {
				Model model = ww.getModel();
				Globe globe = model.getGlobe();
				LayerList layerList = model.getLayers();

				if (args.length == 0) {
					layerList.add(new BMNGWMSLayer());

					AbstractLayer graticuleLayer = new UTMGraticuleLayer();
					layerList.add(graticuleLayer);
				} else {
					String demPath = args[0];
					CompoundElevationModel elevationModel = new CompoundElevationModel();
					final LocalElevationModel localElevationModel = new LocalElevationModel();
					localElevationModel.addElevations(demPath);
					elevationModel.addElevationModel(localElevationModel);
					globe.setElevationModel(elevationModel);
					Sector modelSector = localElevationModel.getSector();

					if (args.length == 1) {// texture
						layerList.add(new BMNGWMSLayer());
						layerList.add(new UTMGraticuleLayer());
					} else {
						String imagePath = args[1];

						if (args.length > 2) {// base layer
							String baseMap = args[2];

							RenderableLayer naturalEarthLayer = new RenderableLayer();
							naturalEarthLayer.addRenderable(new SurfaceImage(baseMap, Sector.FULL_SPHERE));
							naturalEarthLayer.setPickEnabled(false);

							layerList.add(naturalEarthLayer);
						} else {
							layerList.add(new BMNGWMSLayer());
							// layerList.add(new BMNGOneImage());
						}

						SurfaceImageLayer imageLayer = new SurfaceImageLayer();
						imageLayer.addImage(imagePath);
						layerList.add(imageLayer);
					}

					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// silent
					}
					WorldWindUtils.zoomTo(ww, modelSector, true);
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}).start();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
