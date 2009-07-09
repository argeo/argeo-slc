package org.argeo.slc.core.execution.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.exec.CommandLine;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class JvmProcess extends SystemCall {
	private Properties systemProperties = new Properties();
	private List<Resource> classpath = new ArrayList<Resource>();
	private List<Resource> pBootClasspath = new ArrayList<Resource>();
	private Resource jvm = null;
	private String mainClass;
	private List<String> jvmArgs = new ArrayList<String>();
	private List<String> args = new ArrayList<String>();

	@Override
	protected CommandLine createCommandLine() {
		final CommandLine cl;
		if (jvm != null)
			cl = new CommandLine(asFile(jvm));
		else
			cl = new CommandLine("java");

		if (pBootClasspath.size() > 0) {
			StringBuffer buf = new StringBuffer("-Xbootclasspath/p:");
			for (Resource res : pBootClasspath) {
				if (buf.length() != 0)
					buf.append(File.pathSeparatorChar);
				buf.append(asFile(res));
			}
			cl.addArgument(buf.toString());
		}

		for (String jvmArg : jvmArgs) {
			cl.addArgument(jvmArg);
		}

		cl.addArgument("-cp");
		StringBuffer buf = new StringBuffer("");
		for (Resource res : classpath) {
			if (buf.length() != 0)
				buf.append(File.pathSeparatorChar);
			buf.append(asFile(res));
		}
		cl.addArgument(buf.toString());

		for (Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
			cl.addArgument("-D" + entry.getKey() + "=" + entry.getValue());
		}

		// Program
		cl.addArgument(mainClass);

		for (String arg : args) {
			cl.addArgument(arg);
		}

		return cl;
	}

	protected File asFile(Resource res) {
		// TODO: implements local copy
		try {
			return res.getFile().getCanonicalFile();
		} catch (IOException e) {
			throw new SlcException("Cannot convert resource to file", e);
		}

	}

	public Properties getSystemProperties() {
		return systemProperties;
	}

	public void setSystemProperties(Properties systemProperties) {
		this.systemProperties = systemProperties;
	}

	public List<Resource> getClasspath() {
		return classpath;
	}

	public void setClasspath(List<Resource> classpath) {
		this.classpath = classpath;
	}

	public List<Resource> getPBootClasspath() {
		return pBootClasspath;
	}

	public void setPBootClasspath(List<Resource> bootClasspath) {
		pBootClasspath = bootClasspath;
	}

	public Resource getJvm() {
		return jvm;
	}

	public void setJvm(Resource jvm) {
		this.jvm = jvm;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public List<String> getJvmArgs() {
		return jvmArgs;
	}

	public void setJvmArgs(List<String> jvmArgs) {
		this.jvmArgs = jvmArgs;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

}
