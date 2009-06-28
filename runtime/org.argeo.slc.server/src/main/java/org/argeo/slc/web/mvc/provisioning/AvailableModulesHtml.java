package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;
import java.io.Writer;

import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.build.NameVersion;

/** List of modules for a distribution. */
public class AvailableModulesHtml extends AbstractAvailableModules {
	@Override
	protected void print(Writer out, String baseUrl, ModularDistribution md)
			throws IOException {
		out.write("<h1>Distribution " + md + "</h1>");

		for (NameVersion nameVersion : md.listModulesNameVersions()) {
			String fileName = nameVersion.getName() + "-"
					+ nameVersion.getVersion() + ".jar";
			String moduleUrl = baseUrl + fileName;
			out
					.write("<a href=\"" + moduleUrl + "\">" + fileName
							+ "</a><br/>");
		}
	}

	@Override
	public String getContentType() {
		return "text/html";
	}

}
