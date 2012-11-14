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
package org.argeo.slc.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class AntRun implements Runnable {
	private final static Log log = LogFactory.getLog(AntRun.class);

	private Resource buildFile;
	private File baseDir;

	private List<String> targets = new ArrayList<String>();
	private Map<Object, Object> properties = new HashMap<Object, Object>();

	public void run() {
		Project project = new Project();

		try {
			String path = buildFile.getURL().getPath();
			project.setUserProperty("ant.file", path);
			project.setBaseDir(extractBaseDir(path));

			project.init();
			ProjectHelper projectHelper = new ProjectHelper2();
			project.addReference(ProjectHelper.PROJECTHELPER_REFERENCE,
					projectHelper);
			projectHelper.parse(project, buildFile.getURL());
		} catch (Exception e) {
			throw new SlcException("Could not parse " + buildFile, e);
		}

		if (properties != null) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				project.setUserProperty(entry.getKey().toString(), entry
						.getValue().toString());
			}
		}

		project.fireBuildStarted();
		Throwable exception = null;
		try {
			project.addBuildListener(new LoggingListener());
			if (targets.size() == 0) {
				project.executeTarget(project.getDefaultTarget());
			} else {
				project.executeTargets(new Vector<String>(targets));
			}
		} catch (Throwable e) {
			exception = e;
			throw new SlcException("Could not run Ant script " + buildFile, e);
		} finally {
			project.fireBuildFinished(exception);
		}
	}

	private File extractBaseDir(String path) {
		if(this.baseDir!=null)
			return this.baseDir;
		
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

	public void setBuildFile(Resource buildFile) {
		this.buildFile = buildFile;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	public void setProperties(Map<Object, Object> properties) {
		this.properties = properties;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	protected static class LoggingListener implements BuildListener {

		public void buildFinished(BuildEvent event) {
			if (log.isDebugEnabled())
				log.debug("Ant build finished: " + event);
		}

		public void buildStarted(BuildEvent event) {
			if (log.isDebugEnabled())
				log.debug("Ant build started: " + event);
		}

		public void messageLogged(BuildEvent event) {
			if (event.getPriority() == Project.MSG_DEBUG) {
				if (log.isTraceEnabled())
					log.trace(event.getMessage());
			} else if (event.getPriority() == Project.MSG_VERBOSE) {
				if (log.isDebugEnabled())
					log.debug(event.getMessage());
			} else if (event.getPriority() == Project.MSG_INFO) {
				log.info(event.getMessage());

			} else if (event.getPriority() == Project.MSG_WARN) {
				log.warn(event.getMessage());

			} else if (event.getPriority() == Project.MSG_ERR) {
				log.error(event.getMessage());
			} else {
				log.error(event.getMessage());
			}
		}

		public void targetFinished(BuildEvent event) {
			if (log.isTraceEnabled())
				log.debug("Target finished: " + event.getTarget());
		}

		public void targetStarted(BuildEvent event) {
			if (log.isTraceEnabled())
				log.debug("Target started: " + event.getTarget());
		}

		public void taskFinished(BuildEvent event) {
		}

		public void taskStarted(BuildEvent event) {
		}
	}
}
