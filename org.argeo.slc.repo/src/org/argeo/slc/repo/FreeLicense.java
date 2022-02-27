package org.argeo.slc.repo;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.build.License;

/** A free software license */
public abstract class FreeLicense implements License {
	final static String RESOURCES = "/org/argeo/slc/repo/license/";

	/** GNU */
	public final static FreeLicense GPL_v3 = new FreeLicense("GPL-3.0-or-later",
			"http://www.gnu.org/licenses/gpl-3.0.txt", null, RESOURCES + "gpl-3.0.txt") {
	};

	public final static FreeLicense GPL_v2 = new FreeLicense("GPL-2.0-or-later",
			"http://www.gnu.org/licenses/gpl-2.0.txt", null, RESOURCES + "gpl-2.0.txt") {
	};
	public final static FreeLicense GPL = GPL_v3;

	public final static FreeLicense LGPL_v3 = new FreeLicense("LGPL-3.0-or-later",
			"http://www.gnu.org/licenses/lgpl-3.0.txt", null, RESOURCES + "lgpl-3.0.txt") {
	};

	public final static FreeLicense LGPL_v2 = new FreeLicense("LGPL-2.0-or-later",
			"http://www.gnu.org/licenses/lgpl-2.1.txt", null, RESOURCES + "lgpl-2.1.txt") {
	};
	public final static FreeLicense LGPL = LGPL_v3;

	/** Apache */
	public final static FreeLicense APACHE_v2 = new FreeLicense("Apache-2.0",
			"http://www.apache.org/licenses/LICENSE-2.0.txt", null, RESOURCES + "apache-2.0.txt") {
	};
	public final static FreeLicense APACHE = APACHE_v2;

	/** Eclipse */
	public final static FreeLicense EPL_v1 = new FreeLicense("EPL-1.0", "http://www.eclipse.org/legal/epl-v10.html",
			null, RESOURCES + "epl-1.0.txt") {
	};
	public final static FreeLicense EPL_v2 = new FreeLicense("EPL-2.0", "http://www.eclipse.org/legal/epl-v20.html",
			null, RESOURCES + "epl-1.0.txt") {
	};
	public final static FreeLicense EPL = EPL_v1;

	/** Miscellaneous */
	public final static FreeLicense MIT = new FreeLicense("MIT", "http://opensource.org/licenses/MIT", null,
			RESOURCES + "mit.txt") {
	};

	public final static FreeLicense BSD_NEW = new FreeLicense("BSD-3-Clause",
			"http://opensource.org/licenses/BSD-3-Clause", null, RESOURCES + "bsd-3-clause.txt") {
	};

	public final static FreeLicense BSD = BSD_NEW;

	public final static FreeLicense CDDL_v1 = new FreeLicense("CDDL-1.0", "http://opensource.org/licenses/CDDL-1.0",
			null, RESOURCES + "cddl-1.0.txt") {
	};
	public final static FreeLicense CDDL = CDDL_v1;

	public final static FreeLicense MOZILLA_v2 = new FreeLicense("MPL-2.0", "https://opensource.org/licenses/MPL-2.0",
			null, RESOURCES + "cddl-1.0.txt") {
	};
	public final static FreeLicense MOZILLA = MOZILLA_v2;

	/** Public domain corner case */
	public final static License PUBLIC_DOMAIN = new License() {

		public String getUri() {
			return "http://creativecommons.org/about/pdm";
		}

		public String getText() {
			return "This work is free of known copyright restrictions.";
		}

		public String getName() {
			return "Public Domain License";
		}

		public String getLink() {
			return "http://wiki.creativecommons.org/PDM_FAQ";
		}
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
			throw new SlcException("Cannot retrieve license " + name + " from " + url, e);
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
		StringBuilder sb = new StringBuilder(name != null ? name : uri);
//		if (link != null)
//			sb.append(';').append("link=").append(link);
//		else if (uri != null && name != null)
//			sb.append(';').append("link=").append(uri);
		return sb.toString();
	}
}
