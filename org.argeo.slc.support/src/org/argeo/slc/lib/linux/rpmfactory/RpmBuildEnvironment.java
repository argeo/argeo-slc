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
package org.argeo.slc.lib.linux.rpmfactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.argeo.slc.SlcException;

/**
 * Defines a build environment. This information is typically used by other
 * components performing the various actions related to RPM build.
 */
public class RpmBuildEnvironment {
	static String defaultMacroFiles = "/usr/lib/rpm/macros:/usr/lib/rpm/ia32e-linux/macros:/usr/lib/rpm/redhat/macros:/etc/rpm/macros.*:/etc/rpm/macros:/etc/rpm/ia32e-linux/macros:~/.rpmmacros";

	private Map<String, String> rpmmacros = new HashMap<String, String>();

	private List<String> archs = new ArrayList<String>();

	private String stagingBase = "/mnt/slc/repos/rpm";

	/** Write (topdir)/rpmmacros and (topdir)/rpmrc */
	public void writeRpmbuildConfigFiles(File topdir) {
		writeRpmbuildConfigFiles(topdir, new File(topdir, "rpmmacros"),
				new File(topdir, "rpmrc"));
	}

	public void writeRpmbuildConfigFiles(File topdir, File rpmmacroFile,
			File rpmrcFile) {
		try {
			List<String> macroLines = new ArrayList<String>();
			macroLines.add("%_topdir " + topdir.getCanonicalPath());
			for (String macroKey : rpmmacros.keySet()) {
				macroLines.add(macroKey + " " + rpmmacros.get(macroKey));
			}
			FileUtils.writeLines(rpmmacroFile, macroLines);

			List<String> rpmrcLines = new ArrayList<String>();
			rpmrcLines.add("include: /usr/lib/rpm/rpmrc");
			rpmrcLines.add("macrofiles: " + defaultMacroFiles + ":"
					+ rpmmacroFile.getCanonicalPath());
			FileUtils.writeLines(rpmrcFile, rpmrcLines);
		} catch (IOException e) {
			throw new SlcException("Cannot write rpmbuild config files", e);
		}

	}

	public Map<String, String> getRpmmacros() {
		return rpmmacros;
	}

	public void setRpmmacros(Map<String, String> rpmmacros) {
		this.rpmmacros = rpmmacros;
	}

	public String getDefaultMacroFiles() {
		return defaultMacroFiles;
	}

	public void setDefaultMacroFiles(String defaultMacroFiles) {
		this.defaultMacroFiles = defaultMacroFiles;
	}

	public void setArchs(List<String> archs) {
		this.archs = archs;
	}

	public List<String> getArchs() {
		return archs;
	}

	public String getStagingBase() {
		return stagingBase;
	}

	public void setStagingBase(String stagingBase) {
		this.stagingBase = stagingBase;
	}
}