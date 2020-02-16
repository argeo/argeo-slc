package org.argeo.slc.repo.osgi;

import org.argeo.slc.DefaultCategoryNameVersion;

/** A module within an archive. */
public class ArchiveWrapperCNV extends DefaultCategoryNameVersion implements Runnable {
	/** Build runnable */
	private ArchiveWrapper build;

	public ArchiveWrapperCNV(String category, String name, String version, ArchiveWrapper build) {
		super(category, name, version);
		this.build = build;
	}

	@Override
	public void run() {
		if (build != null)
			build.run();
	}

	public ArchiveWrapper getBuild() {
		return build;
	}

}
