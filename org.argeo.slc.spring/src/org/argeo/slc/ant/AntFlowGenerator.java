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
