package org.argeo.slc.ant;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.runtime.SlcRuntime;
import org.springframework.core.io.Resource;

public class AntSlcApplication {
	private SlcRuntime slcRuntime;

	private Resource contextLocation;

	public void execute(SlcExecution slcExecution, Properties properties,
			Map<String, Object> references) {
		
		// Ant coordinates
		String script = slcExecution.getAttributes().get(SlcAntConstants.EXECATTR_ANT_FILE);
		String targetList = slcExecution.getAttributes().get(SlcAntConstants.EXECATTR_ANT_TARGETS);
		List<String> targets = new Vector<String>();
		StringTokenizer stTargets = new StringTokenizer(targetList,",");
		while(stTargets.hasMoreTokens()){
			targets.add(stTargets.nextToken());
		}
		
		Project project = new Project();
		
	}
}
