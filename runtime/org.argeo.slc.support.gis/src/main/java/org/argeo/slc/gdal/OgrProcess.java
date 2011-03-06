package org.argeo.slc.gdal;

import java.io.File;

import org.argeo.slc.core.execution.tasks.SystemCall;

public class OgrProcess implements Runnable {
	private File source;
	private File target;
	private String targetSrs;
	private String clipdst;

	public void run() {
		if (runOgr2Ogr()) {
			SystemCall ogr2ogr = new SystemCall("ogr2ogr");
			if (targetSrs != null)
				ogr2ogr.arg("-t_srs", targetSrs);
			if (clipdst != null)
				ogr2ogr.arg("-clipdst", clipdst);

			// target is before source in OGR!
			ogr2ogr.arg(target.getAbsolutePath());
			ogr2ogr.arg(source.getAbsolutePath());

			ogr2ogr.run();
		}
	}

	protected Boolean runOgr2Ogr() {
		return targetSrs != null || clipdst != null;
	}

	public void setTargetSrs(String targetSrs) {
		this.targetSrs = targetSrs;
	}

	public void setClipdst(String clipdst) {
		this.clipdst = clipdst;
	}

	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
	}

	public File getTarget() {
		return target;
	}

	public void setTarget(File target) {
		this.target = target;
	}

	public String getTargetSrs() {
		return targetSrs;
	}

	public String getClipdst() {
		return clipdst;
	}

}
