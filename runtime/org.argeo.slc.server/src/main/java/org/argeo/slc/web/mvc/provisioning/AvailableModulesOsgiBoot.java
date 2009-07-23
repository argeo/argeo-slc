package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;
import java.io.Writer;

import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.build.NameVersion;

/** List of modules for a distribution. */
public class AvailableModulesOsgiBoot extends AbstractAvailableModules {
	private String separator = ",";

	@Override
	protected void print(Writer out, String baseUrl, ModularDistribution md)
			throws IOException {
		for (NameVersion nameVersion : md.listModulesNameVersions()) {
			String fileName = nameVersion.getName() + "-"
					+ nameVersion.getVersion() + ".jar";
			String moduleUrl = baseUrl + fileName;
			out.write(nameVersion.getName() + separator
					+ nameVersion.getVersion() + separator + moduleUrl);
			out.write("\n");
		}
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

}
