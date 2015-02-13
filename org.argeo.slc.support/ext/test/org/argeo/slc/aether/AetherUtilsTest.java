/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.aether;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import junit.framework.TestCase;

public class AetherUtilsTest extends TestCase {
	public void testConvertPathToArtifact() throws Exception {
		checkPathConversion("my.group.id:my-artifactId:pom:1.2.3",
				"/my/group/id/my-artifactId/1.2.3/my-artifactId-1.2.3.pom");
		checkPathConversion("my.group.id:my-artifactId:pom:1.2.3-SNAPSHOT",
				"/my/group/id/my-artifactId/1.2.3-SNAPSHOT/my-artifactId-1.2.3-SNAPSHOT.pom");
		checkPathConversion("my.group.id:my-artifactId:pom:myClassifier:1.2.3",
				"/my/group/id/my-artifactId/1.2.3/my-artifactId-1.2.3-myClassifier.pom");
		checkPathConversion(
				"my.group.id:my-artifactId:pom:myClassifier:1.2.3-SNAPSHOT",
				"/my/group/id/my-artifactId/1.2.3-SNAPSHOT/my-artifactId-1.2.3-SNAPSHOT-myClassifier.pom");
		checkPathConversion(
				"my.group.id:my-artifactId:pom:myClassifier:20110828.223836-2",
				"/my/group/id/my-artifactId/1.2.3-SNAPSHOT/my-artifactId-20110828.223836-2-myClassifier.pom");
	}

	public void testConvertPathToArtifactRealLife() throws Exception {
		checkPathConversion(
				"org.apache.maven.plugins:maven-antrun-plugin:pom:1.1",
				"org/apache/maven/plugins/maven-antrun-plugin/1.1/maven-antrun-plugin-1.1.pom");
		checkPathConversion(
				"org.apache.maven.plugins:maven-plugin-parent:pom:2.0.1",
				"org/apache/maven/plugins/maven-plugin-parent/2.0.1/maven-plugin-parent-2.0.1.pom");
		checkPathConversion(
				"org.apache.avalon.framework:avalon-framework-impl:pom:4.3.1",
				"org/apache/avalon/framework/avalon-framework-impl/4.3.1/avalon-framework-impl-4.3.1.pom");
		checkPathConversion(
				"org.apache.maven.shared:maven-dependency-tree:pom:1.2",
				"org/apache/maven/shared/maven-dependency-tree/1.2/maven-dependency-tree-1.2.pom");
		checkPathConversion(
				"org.argeo.maven.plugins:maven-argeo-osgi-plugin:pom:1.0.33",
				"org/argeo/maven/plugins/maven-argeo-osgi-plugin/1.0.33/maven-argeo-osgi-plugin-1.0.33.pom");
		checkPathConversion(
				"org.apache.maven.plugins:maven-clean-plugin:pom:2.4.1",
				"org/apache/maven/plugins/maven-clean-plugin/2.4.1/maven-clean-plugin-2.4.1.pom");
	}

	protected void checkPathConversion(String expectedArtifact, String path) {
		Artifact artifact = AetherUtils.convertPathToArtifact(path, null);
		if (expectedArtifact == null)
			assertNull(artifact);
		else
			assertEquals(new DefaultArtifact(expectedArtifact), artifact);
	}
}
