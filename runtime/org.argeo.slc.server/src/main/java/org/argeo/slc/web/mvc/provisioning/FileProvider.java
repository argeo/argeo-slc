package org.argeo.slc.web.mvc.provisioning;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class FileProvider {
	private final static Log log = LogFactory.getLog(FileProvider.class);

	private Resource base;

	public void read(String distribution, String name, String version,
			OutputStream out) {
		Resource bundle = getBundle(distribution, name, version);
		InputStream in = null;
		try {
			in = bundle.getInputStream();
			IOUtils.copy(in, out);
		} catch (Exception e) {
			throw new SlcException("Cannot read bundle for " + name + " ("
					+ version + ")",e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public Resource getBundle(String distribution, String name, String version) {
		try {
			String shortVersion = version;
			int indR = version.indexOf("-r");
			if (indR > -1) {
				shortVersion = version.substring(0, indR);
			}

			int indS = shortVersion.indexOf(".SNAPSHOT");
			if (indS > -1) {
				StringBuffer buf = new StringBuffer(shortVersion);
				buf.setCharAt(indS, '-');
				shortVersion = buf.toString();
			}

			if (log.isDebugEnabled())
				log.debug("Short version for " + name + ": " + shortVersion);

			Resource res = base.createRelative("lib/" + name + "-"
					+ shortVersion + ".jar");
			return res;
		} catch (Exception e) {
			throw new SlcException("Cannot get bundle for " + name + " ("
					+ version + ")",e);
		}
	}

	public void setBase(Resource base) {
		this.base = base;
	}

}
