package org.argeo.slc.maven;

import org.apache.maven.cli.MavenCli;
import org.codehaus.plexus.PlexusContainer;

/** Custom Maven CLI, giving access to the underlying Plexus container. */
class CustomCli extends MavenCli {
	private PlexusContainer container;

	@Override
	protected void customizeContainer(PlexusContainer container) {
		this.container = container;
	}

	public PlexusContainer getContainer() {
		return container;
	}

}
