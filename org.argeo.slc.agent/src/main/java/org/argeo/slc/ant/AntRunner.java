package org.argeo.slc.ant;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.springframework.context.ApplicationContext;

public class AntRunner {
	private ApplicationContext context;
	private ProjectHelper projectHelper;
	private URL buildFile;
	private String[] targets;
	private Properties properties;

	public AntRunner() {

	}

	public AntRunner(ApplicationContext context, ProjectHelper projectHelper,
			URL buildFile, String[] targets) {
		super();
		this.context = context;
		this.projectHelper = projectHelper;
		this.buildFile = buildFile;
		this.targets = targets;
	}

	public AntRunner(ApplicationContext context, URL buildFile, String target) {
		super();
		this.context = context;

		BasicSlcProjectHelper basicSlcProjectHelper = new BasicSlcProjectHelper();
		this.projectHelper = basicSlcProjectHelper;
		basicSlcProjectHelper.setContext(context);

		this.buildFile = buildFile;
		this.targets = new String[] { target };
	}

	public void run() {
		Project p = new Project();

		String path = buildFile.getFile();
		p.setUserProperty("ant.file", path);
		p.setBaseDir(extractBaseDir(path));

		p.init();
		p.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, projectHelper);
		projectHelper.parse(p, buildFile);

		if (properties != null) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				p.setUserProperty(entry.getKey().toString(), entry.getValue()
						.toString());
			}
		}

		p.fireBuildStarted();
		Throwable exception = null;
		try {
			if (targets == null) {
				p.executeTarget(p.getDefaultTarget());
			} else {
				p.executeTargets(new Vector<String>(Arrays.asList(targets)));
			}
		} catch (Throwable e) {
			exception = e;
			throw new SlcAntException("Could not run Ant script " + buildFile,
					e);
		} finally {
			p.fireBuildFinished(exception);
		}

	}

	private File extractBaseDir(String path) {
		String baseDir = null;
		if (path.length() > 1) {
			int indx = path.lastIndexOf('/', path.length() - 1);
			if (indx == -1 || indx == 0) {
				baseDir = "/";
			} else {
				baseDir = path.substring(0, indx) + "/";
			}
		} else {
			baseDir = "/";
		}
		File file = new File(baseDir);
		if (file.exists()) {
			return file;
		} else {
			return new File(System.getProperty("user.dir"));
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
