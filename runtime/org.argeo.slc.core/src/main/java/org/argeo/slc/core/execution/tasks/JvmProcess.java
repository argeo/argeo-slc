package org.argeo.slc.core.execution.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class JvmProcess extends SystemCall implements InitializingBean {
	private Properties systemProperties = new Properties();
	private List<Resource> classpath = new ArrayList<Resource>();
	private List<Resource> pBootClasspath = new ArrayList<Resource>();
	private Resource jvm = null;
	private String mainClass;
	private List<String> jvmArgs = new ArrayList<String>();
	private List<String> args = new ArrayList<String>();

	private String systemPropertiesFileProperty = null;
	private String systemPropertiesFileDir = null;
	private String systemPropertiesFileName = null;

	public void afterPropertiesSet() throws Exception {
		List<Object> command = new ArrayList<Object>();
		if (jvm != null)
			command.add(asFile(jvm).getAbsolutePath());
		else
			command.add("java");

		if (pBootClasspath.size() > 0) {
			StringBuffer buf = new StringBuffer("-Xbootclasspath/p:");
			Boolean first = true;
			for (Resource res : pBootClasspath) {
				if (first)
					first = false;
				else
					buf.append(File.pathSeparatorChar);

				buf.append(asFile(res));
			}
			command.add(buf.toString());
		}

		for (String jvmArg : jvmArgs) {
			command.add(jvmArg);
		}

		command.add("-cp");
		StringBuffer buf = new StringBuffer("");
		for (Resource res : classpath) {
			if (buf.length() != 0)
				buf.append(File.pathSeparatorChar);
			buf.append(asFile(res));
		}
		command.add(buf.toString());

		if (systemPropertiesFileProperty == null) {
			// pass system properties as argument
			for (Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
				command.add("-D" + entry.getKey() + "=" + entry.getValue());
			}
		} else {
			// write system properties in a file to work around OS limitations
			// with command line (e.g. Win XP)
			String dir = systemPropertiesFileDir;
			if (dir == null)
				dir = getExecDirToUse();
			String fileName = systemPropertiesFileName;
			if (fileName == null)
				fileName = systemPropertiesFileProperty + ".properties";

			// Write file
			FileOutputStream fos = null;
			File file = new File(dir + File.separator + fileName);
			try {

				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				fos = new FileOutputStream(file);
				systemProperties.store(fos, "Automatically generated by "
						+ getClass());
				command.add("-D" + systemPropertiesFileProperty + "="
						+ file.getCanonicalPath());
			} catch (IOException e) {
				throw new SlcException("Cannot write to system properties to "
						+ file, e);
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}

		// Program
		command.add(mainClass);

		for (String arg : args) {
			command.add(arg);
		}

		setCommand(command);
	}

	protected File asFile(Resource res) {
		try {
			return res.getFile().getCanonicalFile();
		} catch (FileNotFoundException e) {
			return copyToTempFile(res);
		} catch (IOException e) {
			throw new SlcException("Cannot convert resource to file", e);
		}

	}

	protected File copyToTempFile(Resource res) {
		File tempFile;
		FileOutputStream fos;
		try {
			tempFile = File.createTempFile("slcJvmProcess-", res.getFilename());
			tempFile.deleteOnExit();
			fos = new FileOutputStream(tempFile);
			IOUtils.copy(res.getInputStream(), fos);
		} catch (IOException e) {
			throw new SlcException("Cannot copy " + res + " to temp file.", e);
		}
		IOUtils.closeQuietly(fos);
		return tempFile;
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

	public void setSystemPropertiesFileProperty(
			String systemPropertiesFilePropertyName) {
		this.systemPropertiesFileProperty = systemPropertiesFilePropertyName;
	}

	public void setSystemPropertiesFileDir(String systemPropertiesFileDir) {
		this.systemPropertiesFileDir = systemPropertiesFileDir;
	}

	public void setSystemPropertiesFileName(String systemPropertiesFileName) {
		this.systemPropertiesFileName = systemPropertiesFileName;
	}

}
