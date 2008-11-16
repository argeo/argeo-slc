#!/bin/sh

/mnt/wind/dev/tools-linux/eclipse-pde/eclipse -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator -source /home/mbaudier/dev/src/slc/org.argeo.slc.dist/target/p2Repository/   -metadataRepository file:/home/mbaudier/dev/src/slc/org.argeo.slc.dist/target/p2Repository/   -metadataRepositoryName "Ganymede Update Site"    -artifactRepository file:/home/mbaudier/dev/src/slc/org.argeo.slc.dist/target/p2Repository/   -artifactRepositoryName "Ganymede Artifacts"    -noDefaultIUs   -vmargs -Xmx256m
