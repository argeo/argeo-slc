package org.argeo.slc.lib.linux.rpmfactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

public class RpmSpecFile {
	private Resource specFile;

	private String name;
	private String version;
	private String release;
	private Map<String, String> sources = new HashMap<String, String>();
	private Map<String, String> patches = new HashMap<String, String>();

	public RpmSpecFile(Resource specFile) {
		this.specFile = specFile;
		parseSpecFile();
	}

	public void init() {
		parseSpecFile();
	}

	@SuppressWarnings("unchecked")
	protected void parseSpecFile() {
		try {
			List<String> lines = (List<String>) IOUtils.readLines(specFile
					.getInputStream());

			lines: for (String line : lines) {
				int indexSemiColon = line.indexOf(':');
				if (indexSemiColon <= 0)
					continue lines;
				String directive = line.substring(0, indexSemiColon).trim();
				String value = line.substring(indexSemiColon + 1).trim();
				if ("name".equals(directive.toLowerCase()))
					name = value;
				else if ("version".equals(directive.toLowerCase()))
					version = value;
				else if ("release".equals(directive.toLowerCase()))
					release = value;
				else if (directive.toLowerCase().startsWith("source"))
					sources.put(directive, interpret(value));
				else if (directive.toLowerCase().startsWith("patch"))
					patches.put(directive, interpret(value));
			}

		} catch (IOException e) {
			throw new RuntimeException("Cannot parse spec file " + specFile, e);
		}
	}

	protected String interpret(String value) {
		StringBuffer buf = new StringBuffer(value.length());
		StringBuffer currKey = null;
		boolean mayBeKey = false;
		chars: for (char c : value.toCharArray()) {
			if (c == '%')
				mayBeKey = true;
			else if (c == '{') {
				if (mayBeKey)
					currKey = new StringBuffer();
			} else if (c == '}') {
				if (currKey == null)
					continue chars;
				String key = currKey.toString();
				if ("name".equals(key.toLowerCase()))
					buf.append(name);
				else if ("version".equals(key.toLowerCase()))
					buf.append(version);
				else
					buf.append("%{").append(key).append('}');
				currKey = null;
			} else {
				if (currKey != null)
					currKey.append(c);
				else
					buf.append(c);
			}
		}
		return buf.toString();
	}

	public Resource getSpecFile() {
		return specFile;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getRelease() {
		return release;
	}

	public Map<String, String> getSources() {
		return sources;
	}

	public Map<String, String> getPatches() {
		return patches;
	}

}
