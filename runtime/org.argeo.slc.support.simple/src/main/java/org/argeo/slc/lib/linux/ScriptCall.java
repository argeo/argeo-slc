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
package org.argeo.slc.lib.linux;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class ScriptCall extends SystemCall implements InitializingBean {
	private Resource script;
	private List<Object> scriptArgs = new ArrayList<Object>();

	public void afterPropertiesSet() throws Exception {
		initInterpreter();
		for (Object obj : scriptArgs) {
			arg(obj.toString());
		}
		setStdInFile(script);
	}

	protected void initInterpreter() {
		String ext = FilenameUtils.getExtension(script.getFilename());
		if ("sh".equals(ext))
			arg("/bin/sh").arg("-s");
		else if ("pl".equals(ext))
			arg("/usr/bin/perl").arg("/dev/stdin");
		else if ("py".equals(ext))
			arg("/usr/bin/python").arg("-");
		else
			throw new SlcException("Cannot initialize script intepreter for "
					+ script);
	}

	public void setScript(Resource script) {
		this.script = script;
	}

	public void setScriptArgs(List<Object> scriptArgs) {
		this.scriptArgs = scriptArgs;
	}

}
