/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.client.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class ProcessEditorInput implements IEditorInput, IPersistableElement {
	private String processPath;
	private List<String> initialFlowPaths = new ArrayList<String>();
	private Boolean launchImmediately = false;

	/** New empty process */
	public ProcessEditorInput() {
	}

	/** New process with some flows */
	public ProcessEditorInput(List<String> initialFlowPaths,
			Boolean launchImmediately) {
		this.initialFlowPaths = initialFlowPaths;
		this.launchImmediately = launchImmediately;
	}

	/** Existing process */
	public ProcessEditorInput(String processPath) {
		this.processPath = processPath;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class arg0) {
		return null;
	}

	public boolean exists() {
		return processPath != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return processPath != null ? processPath : "<new process>";
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public String getToolTipText() {
		return "";
	}

	public void saveState(IMemento memento) {
		memento.putString("processPath", processPath);
	}

	public String getFactoryId() {
		return ProcessEditorInputFactory.ID;
	}

	public String getProcessPath() {
		return processPath;
	}

	public List<String> getInitialFlowPaths() {
		return initialFlowPaths;
	}

	public Boolean getLaunchImmediately() {
		return launchImmediately;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProcessEditorInput))
			return false;
		ProcessEditorInput pei = (ProcessEditorInput) obj;
		if (processPath != null && pei.processPath != null)
			return processPath.equals(pei.processPath);
		return false;
	}

}
