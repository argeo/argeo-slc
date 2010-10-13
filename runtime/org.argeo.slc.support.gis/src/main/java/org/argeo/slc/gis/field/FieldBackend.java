package org.argeo.slc.gis.field;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.media.jai.JAI;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.deploy.DefaultResourceSet;
import org.argeo.slc.core.deploy.ResourceSet;
import org.argeo.slc.geotools.Backend;
import org.argeo.slc.geotools.DataDescriptor;
import org.argeo.slc.geotools.data.PostgisDataDescriptor;
import org.argeo.slc.geotools.data.PostgisLayerDataDescriptor;
import org.argeo.slc.geotools.data.VectorDataDescriptor;
import org.argeo.slc.geotools.data.WorldImageDataDescriptor;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.WorldFileReader;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.StyleBuilder;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.springframework.core.io.Resource;

public class FieldBackend implements Backend {
	private final static Log log = LogFactory.getLog(FieldBackend.class);

	private ResourceSet vectors = new DefaultResourceSet();
	private ResourceSet rasters = new DefaultResourceSet();
	private Set<DataSource> dataSources = new HashSet<DataSource>();

	private ClassLoader jaiImageIoClassLoader;
	private PostgisNGDataStoreFactory postgisDataStoreFactory = new PostgisNGDataStoreFactory();

	private Map<String, Object> cache = Collections
			.synchronizedMap(new HashMap<String, Object>());

	private Set<DataDescriptor> resgisteredData = new HashSet<DataDescriptor>();

	public Set<DataDescriptor> getAvailableData(DataDescriptor dataDescriptor) {
		Set<DataDescriptor> availableData = new HashSet<DataDescriptor>();

		if (dataDescriptor == null) {
			return resgisteredData;
		} else if (dataDescriptor instanceof PostgisDataDescriptor) {
			PostgisDataDescriptor pdd = (PostgisDataDescriptor) dataDescriptor;
			if (pdd.getLayers() == null)
				try {
					for (Name name : loadDataStore(dataDescriptor).getNames()) {
						log.info(name);
//						pdd.getLayers().add(
//								new PostgisLayerDataDescriptor(
//										(PostgisDataDescriptor) dataDescriptor,
//										name.getLocalPart()));
					}
					pdd.setLayers(new HashSet<PostgisLayerDataDescriptor>());
					pdd.getLayers().add(
							new PostgisLayerDataDescriptor(
									(PostgisDataDescriptor) dataDescriptor,
									"ways"));
				} catch (Exception e) {
					log.warn("Could not children of " + dataDescriptor + ": "
							+ e);
				}
			availableData.addAll(pdd.getLayers());
		}
		return availableData;
	}

	public void afterPropertiesSet() {
		for (Resource vector : vectors.listResources().values()) {
			try {
				DataDescriptor dd = new VectorDataDescriptor(vector);
				resgisteredData.add(dd);
			} catch (Exception e) {
				log.warn("Could not load " + vector + ": " + e);
			}
		}
		for (Resource raster : rasters.listResources().values()) {
			try {
				DataDescriptor dd = new WorldImageDataDescriptor(raster);
				resgisteredData.add(dd);
			} catch (Exception e) {
				log.warn("Could not load " + raster + ": " + e);
			}
		}
		for (DataSource dataSource : dataSources) {
			try {
				DataDescriptor dd = new PostgisDataDescriptor(dataSource);
				resgisteredData.add(dd);
			} catch (Exception e) {
				log.warn("Could not load " + dataSource + ": " + e);
			}
		}
	}

	public DataStore loadDataStore(DataDescriptor dataDescriptor) {
		if (cache.containsKey(dataDescriptor.getId()))
			return (DataStore) cache.get(dataDescriptor.getId());

		try {
			if (dataDescriptor instanceof VectorDataDescriptor) {
				DataStore ds = FileDataStoreFinder
						.getDataStore(((VectorDataDescriptor) dataDescriptor)
								.getResource().getURL());
				cache(dataDescriptor.getId(), ds);
				return ds;
			} else if (dataDescriptor instanceof PostgisDataDescriptor) {
				DataSource dataSource = ((PostgisDataDescriptor) dataDescriptor)
						.getDataSource();
				Map params = new HashMap();
				params.put(PostgisNGDataStoreFactory.DATASOURCE.key, dataSource);
				DataStore ds = postgisDataStoreFactory.createDataStore(params);
				cache(dataDescriptor.getId(), ds);
				return ds;
			}
		} catch (Exception e) {
			log.error("Could not load " + dataDescriptor + ": " + e);
			if (log.isTraceEnabled())
				log.trace("Stack", e);
		}
		return null;
	}

	public FeatureSource<SimpleFeatureType, SimpleFeature> loadFeatureSource(
			DataDescriptor dataDescriptor) {
		if (cache.containsKey(dataDescriptor.getId()))
			return (FeatureSource<SimpleFeatureType, SimpleFeature>) cache
					.get(dataDescriptor.getId());

		try {
			if (dataDescriptor instanceof VectorDataDescriptor) {
				FeatureSource<SimpleFeatureType, SimpleFeature> fs = ((FileDataStore) loadDataStore(dataDescriptor))
						.getFeatureSource();
				cache(dataDescriptor.getId(), fs);
				return fs;
			} else if (dataDescriptor instanceof PostgisLayerDataDescriptor) {
				PostgisLayerDataDescriptor pgldd = (PostgisLayerDataDescriptor) dataDescriptor;
				FeatureSource<SimpleFeatureType, SimpleFeature> fs = loadDataStore(
						pgldd.getPostGisDataDescriptor()).getFeatureSource(
						pgldd.getName());
				cache(dataDescriptor.getId(), fs);
				return fs;
			}
		} catch (Exception e) {
			log.error("Could not load " + dataDescriptor + ": " + e);
			if (log.isTraceEnabled())
				log.trace("Stack", e);
		}
		return null;
	}

	public GridCoverage loadGridCoverage(DataDescriptor dataDescriptor) {
		if (cache.containsKey(dataDescriptor.getId()))
			return (GridCoverage) cache.get(dataDescriptor.getId());

		if (jaiImageIoClassLoader == null) {
			log.warn("No JAI ImageIO class loader available");
			return null;
		}
		ClassLoader currentContextCl = Thread.currentThread()
				.getContextClassLoader();

		try {
			if (dataDescriptor instanceof WorldImageDataDescriptor) {
				Resource raster = ((WorldImageDataDescriptor) dataDescriptor)
						.getResource();

				Thread.currentThread().setContextClassLoader(
						jaiImageIoClassLoader);
				JAI.getDefaultInstance()
						.getOperationRegistry()
						.registerServices(
								WorldFileReader.class.getClassLoader());
				StyleBuilder styleBuilder = new StyleBuilder();
				RasterSymbolizer rs = styleBuilder.createRasterSymbolizer();
				rs.setGeometryPropertyName("geom");
				WorldImageFormat worldImageFormat = new WorldImageFormat();
				GridCoverage gridCoverage = worldImageFormat.getReader(
						raster.getURL()).read(null);
				cache(dataDescriptor.getId(), gridCoverage);
				return gridCoverage;
			}
		} catch (Exception e) {
			log.error("Could not load grid coverage " + dataDescriptor + ": "
					+ e);
			if (log.isTraceEnabled())
				log.trace("Stack", e);
		} finally {
			Thread.currentThread().setContextClassLoader(currentContextCl);
		}
		return null;
	}

	protected void cache(String id, Object obj) {
		cache.put(id, obj);
	}

	public void setVectors(ResourceSet vectors) {
		this.vectors = vectors;
	}

	public void setRasters(ResourceSet rasters) {
		this.rasters = rasters;
	}

	public void setDataSources(Set<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public void setJaiImageIoClassLoader(ClassLoader jaiImageIoClassLoader) {
		this.jaiImageIoClassLoader = jaiImageIoClassLoader;
	}

}
