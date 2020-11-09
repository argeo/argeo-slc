package org.argeo.slc.client.ui.dist.editors;

import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * Manage a modular distribution version contained in a specific workspace of a
 * repository
 */
public class ModularDistVersionEditor extends ArtifactVersionEditor {
	private static final long serialVersionUID = -2223542780164288554L;

	// private final static Log log =
	// LogFactory.getLog(ModularDistVersionEditor.class);
	public final static String ID = DistPlugin.PLUGIN_ID + ".modularDistVersionEditor";

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}

	@Override
	protected void addPages() {
		setPartName(getFormattedName());
		try {
			addPage(new ModularDistVersionOverviewPage(this, "Modules ",
					getArtifact()));
			addPage(new RunInOsgiPage(this, "Run as OSGi ", getArtifact()));
			addPage(new ModularDistVersionDetailPage(this, "Details",
					getArtifact()));
		} catch (PartInitException e) {
			throw new SlcException("Cannot add distribution editor pages", e);
		}
	}

	protected String getFormattedName() {
		try {
			String partName = null;
			if (getArtifact().hasProperty(SLC_NAME))
				partName = getArtifact().getProperty(SLC_NAME).getString();
			else
				partName = getArtifact().getName();

			if (partName.length() > 10) {
				partName = "..." + partName.substring(partName.length() - 10);
			}
			return partName;
		} catch (RepositoryException re) {
			throw new SlcException("unable to get slc:name property for node "
					+ getArtifact(), re);
		}
	}

}