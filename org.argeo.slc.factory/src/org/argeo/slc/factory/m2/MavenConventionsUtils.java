package org.argeo.slc.factory.m2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * Static utilities around Maven which are NOT using the Maven APIs (conventions
 * based).
 */
public class MavenConventionsUtils {
	public final static String MAVEN_CENTRAL_BASE_URL = "https://repo1.maven.org/maven2/";

	/**
	 * Path to the file identified by this artifact <b>without</b> using Maven APIs
	 * (convention based). Default location of repository (~/.m2/repository) is used
	 * here.
	 * 
	 * @see MavenConventionsUtils#artifactToFile(String, Artifact)
	 */
	public static File artifactToFile(Artifact artifact) {
		return artifactToFile(System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository",
				artifact);
	}

	/**
	 * Path to the file identified by this artifact <b>without</b> using Maven APIs
	 * (convention based).
	 * 
	 * @param repositoryPath path to the related local repository location
	 * @param artifact       the artifact
	 */
	public static File artifactToFile(String repositoryPath, Artifact artifact) {
		return new File(repositoryPath + File.separator + artifact.getGroupId().replace('.', File.separatorChar)
				+ File.separator + artifact.getArtifactId() + File.separator + artifact.getVersion() + File.separator
				+ artifactFileName(artifact)).getAbsoluteFile();
	}

	/** The file name of this artifact when stored */
	public static String artifactFileName(Artifact artifact) {
		return artifact.getArtifactId() + '-' + artifact.getVersion()
				+ (artifact.getClassifier().equals("") ? "" : '-' + artifact.getClassifier()) + '.'
				+ artifact.getExtension();
	}

	/** Absolute path to the file */
	public static String artifactPath(String artifactBasePath, Artifact artifact) {
		return artifactParentPath(artifactBasePath, artifact) + '/' + artifactFileName(artifact);
	}

	/** Absolute path to the file */
	public static String artifactUrl(String repoUrl, Artifact artifact) {
		if (repoUrl.endsWith("/"))
			return repoUrl + artifactPath("/", artifact).substring(1);
		else
			return repoUrl + artifactPath("/", artifact);
	}

	/** Absolute path to the file */
	public static URL mavenCentralUrl(Artifact artifact) {
		String url = artifactUrl(MAVEN_CENTRAL_BASE_URL, artifact);
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			// it should not happen
			throw new IllegalStateException(e);
		}
	}

	/** Absolute path to the directories where the files will be stored */
	public static String artifactParentPath(String artifactBasePath, Artifact artifact) {
		return artifactBasePath + (artifactBasePath.endsWith("/") ? "" : "/") + artifactParentPath(artifact);
	}

	/** Absolute path to the directory of this group */
	public static String groupPath(String artifactBasePath, String groupId) {
		return artifactBasePath + (artifactBasePath.endsWith("/") ? "" : "/") + groupId.replace('.', '/');
	}

	/** Relative path to the directories where the files will be stored */
	public static String artifactParentPath(Artifact artifact) {
		return artifact.getGroupId().replace('.', '/') + '/' + artifact.getArtifactId() + '/'
				+ artifact.getBaseVersion();
	}

	public static String artifactsAsDependencyPom(Artifact pomArtifact, Set<Artifact> artifacts, Artifact parent) {
		StringBuffer p = new StringBuffer();

		// XML header
		p.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		p.append(
				"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		p.append("<modelVersion>4.0.0</modelVersion>\n");

		// Artifact
		if (parent != null) {
			p.append("<parent>\n");
			p.append("<groupId>").append(parent.getGroupId()).append("</groupId>\n");
			p.append("<artifactId>").append(parent.getArtifactId()).append("</artifactId>\n");
			p.append("<version>").append(parent.getVersion()).append("</version>\n");
			p.append("</parent>\n");
		}
		p.append("<groupId>").append(pomArtifact.getGroupId()).append("</groupId>\n");
		p.append("<artifactId>").append(pomArtifact.getArtifactId()).append("</artifactId>\n");
		p.append("<version>").append(pomArtifact.getVersion()).append("</version>\n");
		p.append("<packaging>pom</packaging>\n");

		// Dependencies
		p.append("<dependencies>\n");
		for (Artifact a : artifacts) {
			p.append("\t<dependency>");
			p.append("<artifactId>").append(a.getArtifactId()).append("</artifactId>");
			p.append("<groupId>").append(a.getGroupId()).append("</groupId>");
			if (!a.getExtension().equals("jar"))
				p.append("<type>").append(a.getExtension()).append("</type>");
			p.append("</dependency>\n");
		}
		p.append("</dependencies>\n");

		// Dependency management
		p.append("<dependencyManagement>\n");
		p.append("<dependencies>\n");
		for (Artifact a : artifacts) {
			p.append("\t<dependency>");
			p.append("<artifactId>").append(a.getArtifactId()).append("</artifactId>");
			p.append("<version>").append(a.getVersion()).append("</version>");
			p.append("<groupId>").append(a.getGroupId()).append("</groupId>");
			if (a.getExtension().equals("pom")) {
				p.append("<type>").append(a.getExtension()).append("</type>");
				p.append("<scope>import</scope>");
			}
			p.append("</dependency>\n");
		}
		p.append("</dependencies>\n");
		p.append("</dependencyManagement>\n");

		// Repositories
		// p.append("<repositories>\n");
		// p.append("<repository><id>argeo</id><url>http://maven.argeo.org/argeo</url></repository>\n");
		// p.append("</repositories>\n");

		p.append("</project>\n");
		return p.toString();
	}

	/** Singleton */
	private MavenConventionsUtils() {
	}
}
