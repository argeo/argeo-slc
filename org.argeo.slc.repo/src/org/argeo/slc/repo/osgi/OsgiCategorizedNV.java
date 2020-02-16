package org.argeo.slc.repo.osgi;

import org.argeo.slc.DefaultCategorizedNameVersion;

/** */
class OsgiCategorizedNV extends DefaultCategorizedNameVersion implements Runnable {
	/** Build runnable */
	private ArchiveWrapper build;

	public OsgiCategorizedNV(String category, String name, String version, ArchiveWrapper build) {
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
