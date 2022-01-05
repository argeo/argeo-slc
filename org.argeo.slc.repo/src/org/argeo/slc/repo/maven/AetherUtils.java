package org.argeo.slc.repo.maven;

import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.slc.SlcException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.DependencyNode;

/** Utilities related to Aether */
public class AetherUtils {
	public final static String SNAPSHOT = "SNAPSHOT";
	// hacked from aether
	public static final Pattern SNAPSHOT_TIMESTAMP = Pattern
			.compile("^(.*-)?([0-9]{8}.[0-9]{6}-[0-9]+)$");

	private final static CmsLog log = CmsLog.getLog(AetherUtils.class);

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

	/**
	 * Converts a path (relative to a repository root) to an {@link Artifact}.
	 * 
	 * @param path
	 *            the relative path
	 * @param type
	 *            the layout type, currently ignored because only the 'default'
	 *            Maven 2 layout is currently supported:
	 *            /my/group/id/artifactId/
	 *            version/artifactId-version[-classifier].extension
	 * @return the related artifact or null if the file is not an artifact
	 *         (Maven medata data XML files, check sums, etc.)
	 */
	public static Artifact convertPathToArtifact(String path, String type) {
		// TODO rewrite it with regexp (unit tests first!)

		// normalize
		if (path.startsWith("/"))
			path = path.substring(1);

		// parse group id
		String[] tokensSlash = path.split("/");
		if (tokensSlash.length < 4)
			return null;
		StringBuffer groupId = new StringBuffer(path.length());
		for (int i = 0; i < tokensSlash.length - 3; i++) {
			if (i != 0)
				groupId.append('.');
			groupId.append(tokensSlash[i]);
		}
		String artifactId = tokensSlash[tokensSlash.length - 3];
		String baseVersion = tokensSlash[tokensSlash.length - 2];
		String fileName = tokensSlash[tokensSlash.length - 1];

		if (!fileName.startsWith(artifactId))
			return null;
		// FIXME make it configurable? (via an argument?)
		if (FilenameUtils.isExtension(fileName, new String[] { "sha1", "md5" }))
			return null;

		String extension = FilenameUtils.getExtension(fileName);
		String baseName = FilenameUtils.getBaseName(fileName);

		// check since we assume hereafter
		if (!baseName.startsWith(artifactId))
			throw new SlcException("Base name '" + baseName
					+ " does not start with artifact id '" + artifactId
					+ "' in " + path);

		boolean isSnapshot = baseVersion.endsWith("-" + SNAPSHOT);
		String baseBaseVersion = isSnapshot ? baseVersion.substring(0,
				baseVersion.length() - SNAPSHOT.length() - 1) : baseVersion;
		int artifactAndBaseBaseVersionLength = artifactId.length() + 1
				+ baseBaseVersion.length() + 1;
		String classifier = null;
		if (baseName.length() > artifactAndBaseBaseVersionLength) {
			String dashRest = baseName
					.substring(artifactAndBaseBaseVersionLength);
			String[] dashes = dashRest.split("-");

			if (isSnapshot) {
				if (dashes[0].equals(SNAPSHOT)) {
					if (dashRest.length() > SNAPSHOT.length() + 1)
						classifier = dashRest.substring(SNAPSHOT.length() + 1);

				} else {
					if (dashes.length > 2)// assume no '-' in classifier
						classifier = dashes[2];
				}
			} else {
				if (dashes.length > 0)
					classifier = dashes[0];
			}
		}

		// classifier
		// String classifier = null;
		// int firstDash = baseName.indexOf('-');
		// int classifierDash = baseName.lastIndexOf('-');
		// if (classifierDash > 0 && classifierDash != firstDash) {
		// classifier = baseName.substring(classifierDash + 1);
		// }
		// if (isSnapshot && classifier != null) {
		// if (classifier.equals(SNAPSHOT))
		// classifier = null;
		// else
		// try {
		// Long.parseLong(classifier); // build number
		// // if not failed this is a timestamped version
		// classifier = null;
		// } catch (NumberFormatException e) {
		// // silent
		// }
		// }

		// version
		String version = baseName.substring(artifactId.length() + 1);
		if (classifier != null)
			version = version.substring(0,
					version.length() - classifier.length() - 1);

		// consistency checks
		if (!isSnapshot && !version.equals(baseVersion))
			throw new SlcException("Base version '" + baseVersion
					+ "' and version '" + version + "' not in line in " + path);
		if (!isSnapshot && isSnapshotVersion(version))
			throw new SlcException("SNAPSHOT base version '" + baseVersion
					+ "' and version '" + version + "' not in line in " + path);

		DefaultArtifact artifact = new DefaultArtifact(groupId.toString(),
				artifactId, classifier, extension, version);
		return artifact;
	}

	/** Hacked from aether */
	public static boolean isSnapshotVersion(String version) {
		return version.endsWith(SNAPSHOT)
				|| SNAPSHOT_TIMESTAMP.matcher(version).matches();
	}

}
