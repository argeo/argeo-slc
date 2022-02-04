package org.argeo.slc;

public enum ManifestConstants {
	// OSGi
	BUNDLE_SYMBOLICNAME("Bundle-SymbolicName"), //
	BUNDLE_VERSION("Bundle-Version"), //
	// SLC
	SLC_CATEGORY("SLC-Category"), //
	SLC_ORIGIN_M2("SLC-Origin-M2"), //
	SLC_ORIGIN_MANIFEST_NOT_MODIFIED("SLC-Origin-ManifestNotModified"), //
	SLC_ORIGIN_URI("SLC-Origin-URI"),//
	;

	final String value;

	private ManifestConstants(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
