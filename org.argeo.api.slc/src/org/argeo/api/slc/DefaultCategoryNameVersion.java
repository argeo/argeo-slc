package org.argeo.api.slc;

/** Canonical implementation of {@link CategoryNameVersion} */
public class DefaultCategoryNameVersion extends DefaultNameVersion implements CategoryNameVersion {
	private String category;

	public DefaultCategoryNameVersion() {
	}

	public DefaultCategoryNameVersion(String category, String name, String version) {
		super(name, version);
		this.category = category;
	}

	public DefaultCategoryNameVersion(String category, NameVersion nameVersion) {
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
