package org.argeo.api.slc;

public enum ManifestConstants {
	// OSGi
	BUNDLE_SYMBOLICNAME("Bundle-SymbolicName"), //
	BUNDLE_VERSION("Bundle-Version"), //
	BUNDLE_LICENSE("Bundle-License"), //
	EXPORT_PACKAGE("Export-Package"), //
	IMPORT_PACKAGE("Import-Package"), //
	// JAVA
	AUTOMATIC_MODULE_NAME("Automatic-Module-Name"), //
	// SLC
	SLC_CATEGORY("SLC-Category"), //
	SLC_ORIGIN_M2("SLC-Origin-M2"), //
	SLC_ORIGIN_M2_MERGE("SLC-Origin-M2-Merge"), //
	SLC_ORIGIN_M2_REPO("SLC-Origin-M2-Repo"), //
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
