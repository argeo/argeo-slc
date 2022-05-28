package org.argeo.slc.cms.deploy;

import java.nio.file.Path;

import org.argeo.slc.deploy.TargetData;

public class SimpleCmsTargetData implements TargetData {
	private Integer httpPort;
	private Path instanceData;
}
