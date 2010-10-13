package org.argeo.slc.geotools;

import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public interface Backend {

	public Set<DataDescriptor> getAvailableData(DataDescriptor dataDescriptor);

	public DataStore loadDataStore(DataDescriptor dataDescriptor);

	public FeatureSource<SimpleFeatureType, SimpleFeature> loadFeatureSource(
			DataDescriptor dataDescriptor);

	public GridCoverage loadGridCoverage(DataDescriptor dataDescriptor);
}
