package org.argeo.slc;

/**
 * Adds a dimension to {@link NameVersion} by adding an arbitrary category (e.g.
 * Maven groupId, yum repository ID, etc.)
 */
public interface CategoryNameVersion extends NameVersion {
	/** The category of the component. */
	String getCategory();

	static CategoryNameVersion parseCategoryNameVersion(String str) {
		if (str == null || "".equals(str.trim()))
			throw new IllegalArgumentException("At least one character required.");
		String[] arr = str.trim().split(":");
		if (arr.length > 3)
			throw new IllegalArgumentException(str + " does not respect the [category]:[name]:[version] pattern");
		DefaultCategoryNameVersion res = new DefaultCategoryNameVersion();
		res.setCategory(arr[0]);
		if (arr.length > 1)
			res.setName(arr[1]);
		if (arr.length > 2)
			res.setVersion(arr[2]);
		return res;
	}
}
