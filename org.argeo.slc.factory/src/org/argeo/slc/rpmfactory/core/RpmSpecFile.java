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
package org.argeo.slc.rpmfactory.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpmSpecFile {
	private Path specFile;

	private String name;
	private String version;
	private String release;
	private Map<String, String> sources = new HashMap<String, String>();
	private Map<String, String> patches = new HashMap<String, String>();

	public RpmSpecFile(Path specFile) {
		this.specFile = specFile;
		parseSpecFile();
	}

	public void init() {
		parseSpecFile();
	}

	protected void parseSpecFile() {
		try {
			List<String> lines = (List<String>) Files.readAllLines(specFile);

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

	public Path getSpecFile() {
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
