package org.argeo.slc.repo.osgi;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;

/**
 * Wraps an OSGi profile, simplifying access to its values such as system
 * packages, etc.
 */
public class OsgiProfile {
	public final static String PROP_SYSTEM_PACKAGES = "org.osgi.framework.system.packages";

	public final static OsgiProfile PROFILE_JAVA_SE_1_6 = new OsgiProfile("JavaSE-1.6.profile");

	private final URL url;
	private final Properties properties;

	public OsgiProfile(URL url) {
		this.url = url;
		properties = new Properties();
		InputStream in = null;
		try {
			properties.load(this.url.openStream());
		} catch (Exception e) {
			throw new SlcException("Cannot initalize OSGi profile " + url, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public OsgiProfile(String name) {
		this(OsgiProfile.class.getClassLoader()
				.getResource('/' + OsgiProfile.class.getPackage().getName().replace('.', '/') + '/' + name));
	}

	public List<String> getSystemPackages() {
		String[] splitted = properties.getProperty(PROP_SYSTEM_PACKAGES).split(",");
		List<String> res = new ArrayList<String>();
		for (String pkg : splitted) {
			res.add(pkg.trim());
		}
		return Collections.unmodifiableList(res);
	}
}
