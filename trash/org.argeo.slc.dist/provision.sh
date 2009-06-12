#!/bin/sh

ECLIPSE_BASE=/home/mbaudier/lib64/eclipse-3.4
$ECLIPSE_BASE/eclipse \
	-application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator \
	-source ./target/p2Repository/  \
	-metadataRepository file:/home/mbaudier/dev/work/p2Tests/repository  \
	-metadataRepositoryName "Argeo SLC Update Site"  \
	-artifactRepository file:/home/mbaudier/dev/work/p2Tests/repository \
	-artifactRepositoryName "Argeo SLC Artifacts" \
	-publishArtifacts \
	-publishArtifactRepository \
	-root org.argeo.slc.runtime \
	-rootVersion 0.11.2.SNAPSHOT \
	-noDefaultIUs  \
	-noSplash \
	-vmargs \
	-Xmx256m
