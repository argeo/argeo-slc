package org.argeo.slc.ant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.argeo.slc.core.execution.AbstractExecutionFlowGenerator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;

public class AntFlowGenerator extends AbstractExecutionFlowGenerator {
	private List<Resource> antFiles = new ArrayList<Resource>();

	protected Map<String, BeanDefinition> createExecutionFlowDefinitions(
			ConfigurableListableBeanFactory beanFactory) {
		Map<String, BeanDefinition> definitions = new HashMap<String, BeanDefinition>();

		for (Resource antFile : antFiles) {
			AntRun antRun = new AntRun();
			antRun.setBuildFile(antFile);

			List<Runnable> executables = new ArrayList<Runnable>();
			executables.add(antRun);
			definitions.put("ant." + antFile.getFilename(),
					createDefaultFlowDefinition(executables));
		}
		return definitions;
	}

	public void setAntFiles(List<Resource> antFiles) {
		this.antFiles = antFiles;
	}

}
