package org.argeo.slc.geotools;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.SlcException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.FactoryBean;

public class BeanFeatureTypeBuilder<T> implements FactoryBean {
	private final BeanWrapper classBeanWrapper;

	private SimpleFeatureType cachedFeatureType;
	private List<String> cachedAttributeList;

	public BeanFeatureTypeBuilder(Class<? extends T> clss) {
		this.classBeanWrapper = new BeanWrapperImpl(clss);
		cachedFeatureType = doBuildFeatureType();
	}

	protected SimpleFeatureType doBuildFeatureType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

		builder
				.setName(getClassBeanWrapper().getWrappedClass()
						.getSimpleName());

		cachedAttributeList = new ArrayList<String>();
		for (PropertyDescriptor pd : getClassBeanWrapper()
				.getPropertyDescriptors()) {
			builder.add(pd.getName(), pd.getPropertyType());
			cachedAttributeList.add(pd.getName());
		}

		// TODO: make it configurable
		builder.setNamespaceURI("http://localhost/");
		builder.setCRS(DefaultGeographicCRS.WGS84);

		return builder.buildFeatureType();
	}

	public SimpleFeatureType getFeatureType() {
		if (cachedFeatureType == null) {
			cachedFeatureType = doBuildFeatureType();
		}
		return cachedFeatureType;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends T> getWrappedClass() {
		return (Class<? extends T>) classBeanWrapper.getWrappedClass();
	}

	protected void resetFeatureType() {
		cachedFeatureType = null;
		if (cachedAttributeList != null) {
			cachedAttributeList.clear();
			cachedAttributeList = null;
		}
	}

	protected List<String> getCachedAttributeList() {
		if (cachedAttributeList == null)
			throw new SlcException(
					"Cached attribute list not set: initialize the object properly before calling this method");
		return cachedAttributeList;
	}

	public SimpleFeature buildFeature(T object) {
		return buildFeature(object, null);
	}

	public SimpleFeature buildFeature(Object object, String id) {
		if (!((Class<?>) classBeanWrapper.getWrappedClass())
				.isAssignableFrom(object.getClass())) {
			throw new SlcException("Object type " + object.getClass()
					+ " not compatible with wrapped class "
					+ classBeanWrapper.getWrappedClass());
		}

		BeanWrapper instanceWrapper = new BeanWrapperImpl(object);
		SimpleFeatureType type = getFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
		for (String attr : getCachedAttributeList()) {
			featureBuilder.add(instanceWrapper.getPropertyValue(attr));
		}

		return featureBuilder.buildFeature(id);
	}

	protected BeanWrapper getClassBeanWrapper() {
		return classBeanWrapper;
	}

	public Object getObject() throws Exception {
		return getFeatureType();
	}

	public Class<?> getObjectType() {
		return classBeanWrapper.getWrappedClass();
	}

	public boolean isSingleton() {
		return true;
	}

}
