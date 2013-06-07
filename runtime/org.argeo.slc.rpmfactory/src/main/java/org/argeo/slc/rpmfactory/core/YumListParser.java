package org.argeo.slc.rpmfactory.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

/**
 * Reads the output of a 'yum list all' command and interpret the list of
 * packages.
 */
public class YumListParser implements RpmPackageSet {
	private final static Log log = LogFactory.getLog(YumListParser.class);

	private Set<String> installed = new TreeSet<String>();
	/** Not installed but available */
	private Set<String> installable = new TreeSet<String>();

	private Resource yumListOutput;

	public void init() {
		try {
			if (yumListOutput != null) {
				load(yumListOutput.getInputStream());
				if (log.isDebugEnabled())
					log.debug(installed.size() + " installed, "
							+ installable.size() + " installable, from "
							+ yumListOutput);
			}
		} catch (IOException e) {
			throw new SlcException("Cannot initialize yum list parser", e);
		}
	}

	public Boolean contains(String packageName) {
		if (installed.contains(packageName))
			return true;
		else
			return installable.contains(packageName);
	}

	protected void load(InputStream in) {
		try {
			Boolean readingInstalled = false;
			Boolean readingAvailable = false;
			LineIterator it = IOUtils.lineIterator(in, "UTF-8");
			while (it.hasNext()) {
				String line = it.nextLine();
				if (line.trim().equals("Installed Packages")) {
					readingInstalled = true;
				} else if (line.trim().equals("Available Packages")) {
					readingAvailable = true;
					readingInstalled = false;
				} else if (readingAvailable) {
					if (Character.isLetterOrDigit(line.charAt(0))) {
						installable.add(extractRpmName(line));
					}
				} else if (readingInstalled) {
					if (Character.isLetterOrDigit(line.charAt(0))) {
						installed.add(extractRpmName(line));
					}
				}
			}
		} catch (IOException e) {
			throw new SlcException("Cannot load yum list output", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	protected String extractRpmName(String line) {
		StringTokenizer st = new StringTokenizer(line, " \t");
		String packageName = st.nextToken();
		// consider the arch as an extension
		return FilenameUtils.getBaseName(packageName);
		//return packageName.split("\\.")[0];
	}

	public Set<String> getInstalled() {
		return installed;
	}

	public Set<String> getInstallable() {
		return installable;
	}

	public void setYumListOutput(Resource yumListOutput) {
		this.yumListOutput = yumListOutput;
	}

}
