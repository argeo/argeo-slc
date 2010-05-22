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
