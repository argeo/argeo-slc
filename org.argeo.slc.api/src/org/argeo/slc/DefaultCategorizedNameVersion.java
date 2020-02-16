package org.argeo.slc;

/** Canonical implementation of {@link CategorizedNameVersion} */
public class DefaultCategorizedNameVersion extends DefaultNameVersion implements CategorizedNameVersion {
	private String category;

	public DefaultCategorizedNameVersion() {
	}

	public DefaultCategorizedNameVersion(String category, String name, String version) {
		super(name, version);
		this.category = category;
	}

	public DefaultCategorizedNameVersion(String category, NameVersion nameVersion) {
		super(nameVersion);
		this.category = category;
	}

	@Override
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return category + ":" + super.toString();
	}

}
