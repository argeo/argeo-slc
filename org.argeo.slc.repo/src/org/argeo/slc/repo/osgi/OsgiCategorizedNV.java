package org.argeo.slc.repo.osgi;

import org.argeo.slc.DefaultCategorizedNameVersion;

/** */
class OsgiCategorizedNV extends DefaultCategorizedNameVersion implements Runnable {
	/** Build runnable */
	private Runnable build;

	public OsgiCategorizedNV(String category, String name, String version, Runnable build) {
		super(category, name, version);
		this.build = build;
	}

	@Override
	public void run() {
		if (build != null)
			build.run();
	}

}
