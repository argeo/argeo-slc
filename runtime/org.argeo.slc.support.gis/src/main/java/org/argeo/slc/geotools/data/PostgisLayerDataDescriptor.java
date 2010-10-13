package org.argeo.slc.geotools.data;

import org.argeo.slc.geotools.AbstractDataDescriptor;

public class PostgisLayerDataDescriptor extends AbstractDataDescriptor
		implements FeatureSourceDataDescriptor {
	private PostgisDataDescriptor postGisDataDescriptor;
	private String name;

	public PostgisLayerDataDescriptor(
			PostgisDataDescriptor postgisDataDescriptor, String name) {
		this.postGisDataDescriptor = postgisDataDescriptor;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public PostgisDataDescriptor getPostGisDataDescriptor() {
		return postGisDataDescriptor;
	}

}
