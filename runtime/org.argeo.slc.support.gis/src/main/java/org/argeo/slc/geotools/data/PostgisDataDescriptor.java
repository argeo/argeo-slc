package org.argeo.slc.geotools.data;

import java.util.Set;

import javax.sql.DataSource;

import org.argeo.slc.geotools.AbstractDataDescriptor;

public class PostgisDataDescriptor extends AbstractDataDescriptor {
	private DataSource dataSource;

	private Set<PostgisLayerDataDescriptor> layers = null;

	public PostgisDataDescriptor(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public Set<PostgisLayerDataDescriptor> getLayers() {
		return layers;
	}

	public void setLayers(Set<PostgisLayerDataDescriptor> layers) {
		this.layers = layers;
	}

}
