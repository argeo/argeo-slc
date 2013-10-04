/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.osgi.build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.ModularDistribution;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;

public abstract class AbstractOsgiModularDistribution implements
		ModularDistribution, BundleContextAware, InitializingBean {
	private final static Log log = LogFactory
			.getLog(AbstractOsgiModularDistribution.class);

	private BundleContext bundleContext;
	private EclipseUpdateSite eclipseUpdateSite;

	/** Initialized by the object itself. */
	private SortedMap<NameVersion, Distribution> distributions = new TreeMap<NameVersion, Distribution>();

	protected abstract void fillDistributions(
			SortedMap<NameVersion, Distribution> distributions)
			throws Exception;

	public Distribution getModuleDistribution(String moduleName,
			String moduleVersion) {
		return distributions.get(new DefaultNameVersion(moduleName,
				moduleVersion));
	}

	public String getDistributionId() {
		return bundleContext.getBundle().getSymbolicName()
				+ "-"
				+ bundleContext.getBundle().getHeaders()
						.get(Constants.BUNDLE_VERSION);
	}

	public Set<NameVersion> listModulesNameVersions() {
		return distributions.keySet();
	}

	public Iterator<NameVersion> nameVersions() {
		return distributions.keySet().iterator();
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void afterPropertiesSet() throws Exception {
		fillDistributions(distributions);
		if (log.isDebugEnabled())
			log.debug("Distribution " + getName() + ":" + getVersion()
					+ " loaded (" + distributions.size() + " modules)");
	}

	protected String findVersion(String name) {
		Set<String> versions = new HashSet<String>();
		for (NameVersion key : distributions.keySet()) {
			if (key.getName().equals(name))
				versions.add(key.getVersion());
		}

		if (versions.size() == 0)
			throw new SlcException("Cannot find version for name " + name);
		else if (versions.size() > 1)
			throw new SlcException("Found more than one version for name "
					+ name + ": " + versions);
		else
			return versions.iterator().next();

	}

	public Object getModulesDescriptor(String descriptorType) {
		if (descriptorType.equals("eclipse"))
			return writeEclipseUpdateSite();
		else
			throw new UnsupportedException("descriptorType", descriptorType);
	}

	protected Set<NameVersion> writePlainUrlList() {
		return distributions.keySet();
	}

	protected String writeEclipseUpdateSite() {
		if (eclipseUpdateSite == null)
			throw new SlcException("No eclipse update site declared.");

		StringBuffer buf = new StringBuffer("");
		buf.append("<site>");

		List<EclipseUpdateSiteCategory> usedCategories = new ArrayList<EclipseUpdateSiteCategory>();
		for (EclipseUpdateSiteFeature feature : eclipseUpdateSite.getFeatures()) {

			String featureId = feature.getName();
			String featureVersion = findVersion(featureId);
			buf.append("<feature");
			buf.append(" url=\"features/").append(featureId).append('_')
					.append(featureVersion).append(".jar\"");
			buf.append(" id=\"").append(featureId).append("\"");
			buf.append(" version=\"").append(featureVersion).append("\"");
			buf.append(">\n");

			for (EclipseUpdateSiteCategory category : feature.getCategories()) {
				usedCategories.add(category);
				buf.append("  <category name=\"").append(category.getName())
						.append("\"/>\n");
			}
			buf.append("</feature>\n\n");
		}

		for (EclipseUpdateSiteCategory category : usedCategories) {
			buf.append("<category-def");
			buf.append(" name=\"").append(category.getName()).append("\"");
			buf.append(" label=\"").append(category.getLabel()).append("\"");
			buf.append(">\n");
			buf.append("  <description>").append(category.getDescription())
					.append("</description>\n");
			buf.append("</category-def>\n\n");
		}

		buf.append("</site>");
		return buf.toString();
	}

	public String getName() {
		return bundleContext.getBundle().getSymbolicName();
	}

	public String getVersion() {
		return bundleContext.getBundle().getHeaders()
				.get(Constants.BUNDLE_VERSION).toString();
	}

	@Override
	public String toString() {
		return new DefaultNameVersion(this).toString();
	}

	public void setEclipseUpdateSite(EclipseUpdateSite eclipseUpdateSite) {
		this.eclipseUpdateSite = eclipseUpdateSite;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

}
