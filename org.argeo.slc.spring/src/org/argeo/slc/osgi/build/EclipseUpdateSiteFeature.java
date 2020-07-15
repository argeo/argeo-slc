package org.argeo.slc.osgi.build;

import java.util.ArrayList;
import java.util.List;

public class EclipseUpdateSiteFeature {
	private String name;
	private List<EclipseUpdateSiteCategory> categories = new ArrayList<EclipseUpdateSiteCategory>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EclipseUpdateSiteCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<EclipseUpdateSiteCategory> categories) {
		this.categories = categories;
	}

}
