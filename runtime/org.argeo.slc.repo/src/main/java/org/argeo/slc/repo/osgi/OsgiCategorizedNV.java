package org.argeo.slc.repo.osgi;

import org.argeo.slc.CategorizedNameVersion;
import org.argeo.slc.DefaultNameVersion;

class OsgiCategorizedNV extends DefaultNameVersion implements
		CategorizedNameVersion, Runnable {
	private String category;
	/** Build runnable */
	private Runnable build;

	public OsgiCategorizedNV(String category, String name, String version,
			Runnable build) {
		super(name, version);
		this.category = category;
		this.build = build;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public void run() {
		if (build != null)
			build.run();
	}

}
