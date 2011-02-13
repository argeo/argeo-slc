package org.argeo.slc.aether;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sonatype.aether.AbstractRepositoryListener;
import org.sonatype.aether.RepositoryEvent;

public class ConsoleRepositoryListener extends AbstractRepositoryListener {

	private final static Log log = LogFactory
			.getLog(ConsoleRepositoryListener.class);

	public void artifactDeployed(RepositoryEvent event) {
		if (log.isDebugEnabled())
			log.debug("Deployed " + event.getArtifact() + " to "
					+ event.getRepository());
	}

	public void artifactDeploying(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Deploying " + event.getArtifact() + " to "
					+ event.getRepository());
	}

	public void artifactDescriptorInvalid(RepositoryEvent event) {
		if (log.isDebugEnabled())
			log.warn("Invalid artifact descriptor for " + event.getArtifact()
					+ ": " + event.getException().getMessage());
	}

	public void artifactDescriptorMissing(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.warn("Missing artifact descriptor for " + event.getArtifact());
	}

	public void artifactInstalled(RepositoryEvent event) {
		if (log.isDebugEnabled())
			log.debug("Installed " + event.getArtifact() + " to "
					+ event.getFile());
	}

	public void artifactInstalling(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Installing " + event.getArtifact() + " to "
					+ event.getFile());
	}

	public void artifactResolved(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Resolved artifact " + event.getArtifact() + " from "
					+ event.getRepository());
	}

	public void artifactDownloading(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Downloading artifact " + event.getArtifact() + " from "
					+ event.getRepository());
	}

	public void artifactDownloaded(RepositoryEvent event) {
		if (log.isDebugEnabled())
			log.debug("Downloaded artifact " + event.getArtifact() + " from "
					+ event.getRepository());
	}

	public void artifactResolving(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Resolving artifact " + event.getArtifact());
	}

	public void metadataDeployed(RepositoryEvent event) {
		if (log.isDebugEnabled())
			log.debug("Deployed " + event.getMetadata() + " to "
					+ event.getRepository());
	}

	public void metadataDeploying(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Deploying " + event.getMetadata() + " to "
					+ event.getRepository());
	}

	public void metadataInstalled(RepositoryEvent event) {
		if (log.isDebugEnabled())
			log.debug("Installed " + event.getMetadata() + " to "
					+ event.getFile());
	}

	public void metadataInstalling(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Installing " + event.getMetadata() + " to "
					+ event.getFile());
	}

	public void metadataInvalid(RepositoryEvent event) {
		if (log.isDebugEnabled())
			log.debug("Invalid metadata " + event.getMetadata());
	}

	public void metadataResolved(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Resolved metadata " + event.getMetadata() + " from "
					+ event.getRepository());
	}

	public void metadataResolving(RepositoryEvent event) {
		if (log.isTraceEnabled())
			log.trace("Resolving metadata " + event.getMetadata() + " from "
					+ event.getRepository());
	}

}
