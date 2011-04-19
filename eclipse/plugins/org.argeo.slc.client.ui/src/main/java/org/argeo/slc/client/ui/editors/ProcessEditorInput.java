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

}
