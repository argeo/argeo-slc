package org.argeo.slc.repo;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.build.License;

/** A free software license */
public abstract class FreeLicense implements License {
	final static String RESOURCES = "/org/argeo/slc/repo/license/";

	public final static FreeLicense GPL_v3 = new FreeLicense(
			"GNU General Public License, version 3.0",
			"http://www.gnu.org/licenses/gpl-3.0.txt",
			"http://www.gnu.org/licenses/", RESOURCES + "gpl-3.0.txt") {
	};

	public final static FreeLicense GPL_v2 = new FreeLicense(
			"GNU General Public License, version 2.0",
			"http://www.gnu.org/licenses/gpl-2.0.txt",
			"http://www.gnu.org/licenses/", RESOURCES + "gpl-2.0.txt") {
	};

	public final static FreeLicense APACHE_v2 = new FreeLicense(
			"Apache License, Version 2.0",
			"http://www.apache.org/licenses/LICENSE-2.0.txt",
			"http://www.apache.org/licenses/", RESOURCES + "apache-2.0.txt") {
	};

	public final static FreeLicense EPL_v1 = new FreeLicense(
			"Eclipse Public License, Version 1.0",
			"http://www.eclipse.org/legal/epl-v10.html",
			"http://www.eclipse.org/legal/eplfaq.php", RESOURCES
					+ "epel-1.0.txt") {
	};

	public final static FreeLicense MIT = new FreeLicense("The MIT License",
			"http://opensource.org/licenses/MIT", null, RESOURCES + "mit.txt") {
	};

	public final static FreeLicense LGPL_v3 = new FreeLicense(
			"GNU Lesser General Public License, version 3.0",
			"http://www.gnu.org/licenses/lgpl-3.0.txt",
			"http://www.gnu.org/licenses/", RESOURCES + "lgpl-3.0.txt") {
	};

	public final static FreeLicense LGPL_v2 = new FreeLicense(
			"GNU Lesser General Public License, version 2.1",
			"http://www.gnu.org/licenses/lgpl-2.1.txt",
			"http://www.gnu.org/licenses/", RESOURCES + "lgpl-2.1.txt") {
	};

	private final String name, uri, link, resource;

	public FreeLicense(String name, String uri) {
		this(name, uri, null, null);
	}

	public FreeLicense(String name, String uri, String link) {
		this(name, uri, link, null);
	}

	public FreeLicense(String name, String uri, String link, String resource) {
		if (uri == null)
			throw new SlcException("URI cannot be null");
		this.name = name;
		this.uri = uri;
		this.link = link;
		this.resource = resource;
		getText();
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public String getLink() {
		return link;
	}

	@Override
	public String getText() {
		InputStream in = null;
		URL url = null;
		try {
			if (resource != null)
				url = getClass().getClassLoader().getResource(resource);
			else
				url = new URL(uri);
			in = url.openStream();
			String text = IOUtils.toString(in);
			return text;
		} catch (Exception e) {
			throw new SlcException("Cannot retrieve license " + name + " from "
					+ url, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof License))
			return false;
		return ((License) obj).getUri().equals(getUri());
	}

	@Override
	public int hashCode() {
		return getUri().hashCode();
	}

	@Override
	public String toString() {
		return name + " (" + uri + ")";
	}
}
