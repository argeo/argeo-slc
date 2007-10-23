package org.argeo.slc.ant;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelperImpl;

public class SlcProjectHelper extends ProjectHelperImpl {
	public static String PROP_APPLICATION_CONTEXT = "org.argeo.slc.slcRootContext";
	public static String REF_ROOT_CONTEXT = "slcApplicationContext";

	@Override
	public void parse(Project project, Object source) throws BuildException {
		stdOut("Entered SLC project helper");

		// call the underlying implementation to do the actual work
		super.parse(project, source);

		String acPath = System.getProperty(PROP_APPLICATION_CONTEXT);
		if (acPath == null) {
			acPath = "applicationContext.xml";
		}
		ApplicationContext context = new FileSystemXmlApplicationContext(acPath);
		project.addReference(REF_ROOT_CONTEXT, context);
	}

	private static void stdOut(Object o) {
		System.out.println(o);
	}

}
