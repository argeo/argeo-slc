package org.argeo.slc.osgi.build;

import java.util.ArrayList;
import java.util.List;

public class EclipseUpdateSite {
	private List<EclipseUpdateSiteFeature> features = new ArrayList<EclipseUpdateSiteFeature>();

	public List<EclipseUpdateSiteFeature> getFeatures() {
		return features;
	}

	public void setFeatures(List<EclipseUpdateSiteFeature> features) {
		this.features = features;
	}

}
