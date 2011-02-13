package org.argeo.slc.aether;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;

/** Utilities related to Aether */
public class AetherUtils {
	private final static Log log = LogFactory.getLog(AetherUtils.class);

	/** Logs a dependency node and its transitive dependencies as a tree. */
	public static void logDependencyNode(int depth,
			DependencyNode dependencyNode) {
		if (!log.isDebugEnabled())
			return;

		StringBuffer prefix = new StringBuffer(depth * 2 + 2);
		// prefix.append("|-");
		for (int i = 0; i < depth * 2; i++) {
			prefix.append(' ');
		}
		Artifact artifact = dependencyNode.getDependency().getArtifact();
		log.debug(prefix + "|-> " + artifact.getArtifactId() + " ["
				+ artifact.getVersion() + "]"
				+ (dependencyNode.getDependency().isOptional() ? " ?" : ""));
		for (DependencyNode child : dependencyNode.getChildren()) {
			logDependencyNode(depth + 1, child);
		}
	}

}
