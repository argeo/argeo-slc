package org.argeo.cms.worldwind.swt;

import java.awt.EventQueue;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

/**
 * A {@link WorldWindow} based on {@link WorldWindowGLCanvas} and the SWT-AWT
 * bridge.
 */
public class WorldWindowSwtAwtCanvas extends WorldWindowGLCanvas {
	private static final long serialVersionUID = 3289283729801847982L;

	public WorldWindowSwtAwtCanvas(Composite parent, int style) {
		this(parent, style, new BasicModel(new Earth(), new LayerList()));
	}

	public WorldWindowSwtAwtCanvas(Composite parent, int style, Model model) {
		Objects.requireNonNull(parent);
		Objects.requireNonNull(model);

		if (getView() instanceof BasicOrbitView orbitView) {
			// needed so that ExampleUtil.goTo(wwd, modelSector) works
			orbitView.setGlobe(model.getGlobe());
		}
		setModel(model);

		Composite embed = new Composite(parent, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame(embed);
		EventQueue.invokeLater(() -> frame.add(this, java.awt.BorderLayout.CENTER));
	}

}
