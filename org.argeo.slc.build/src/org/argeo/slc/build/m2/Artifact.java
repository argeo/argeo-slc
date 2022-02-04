package org.argeo.slc.build.m2;

public interface Artifact {
	String getGroupId();

	String getArtifactId();

	String getVersion();

	default String getBaseVersion() {
		return getVersion();
	}

//	boolean isSnapshot();

	default String getClassifier() {
		return "";
	}

	default String getExtension() {
		return "jar";
	}

}
