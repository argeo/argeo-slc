package org.argeo.slc.e4;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/** Shared images. */
public class SlcImages {
	public final static Image AGENT = img("icons/agent.gif");
	public final static Image AGENT_FACTORY = img("icons/agentFactory.gif");
	public final static Image MODULE = img("icons/module.png");
	public final static Image MODULE_STOPPED = img("icons/module_stopped.gif");
	public final static Image FOLDER = img("icons/folder.gif");
	public final static Image MY_RESULTS_FOLDER = img("icons/myResult.png");
	public final static Image RENAME = img("icons/rename.png");
	public final static Image FLOW = img("icons/flow.png");
	public final static Image PROCESSES = img("icons/processes.gif");
	public final static Image PASSED = img("icons/passed.gif");
	public final static Image ERROR = img("icons/error.gif");
	public final static Image LAUNCH = img("icons/launch.gif");
	public final static Image RELAUNCH = img("icons/relaunch.gif");
	public final static Image KILL = img("icons/kill.png");
	public final static Image REMOVE_ONE = img("icons/remove_one.gif");
	public final static Image REMOVE_ALL = img("icons/removeAll.png");
	public final static Image EXECUTION_SPECS = img("icons/executionSpecs.gif");
	public final static Image EXECUTION_SPEC = img("icons/executionSpec.gif");
	public final static Image EXECUTION_SPEC_ATTRIBUTE = img("icons/executionSpecAttribute.gif");
	public final static Image CHOICES = img("icons/choices.gif");
	public final static Image PROCESS_ERROR = img("icons/process_error.png");
	public final static Image PROCESS_SCHEDULED = img("icons/process_scheduled.gif");
	public final static Image PROCESS_RUNNING = img("icons/process_running.png");
	public final static Image PROCESS_COMPLETED = img("icons/process_completed.png");

	// Decorators
	public final static ImageDescriptor EXECUTION_ERROR = getImageDescriptor("icons/executionError.gif");
	public final static ImageDescriptor EXECUTION_PASSED = getImageDescriptor("icons/executionPassed.gif");

	/** Creates the image */
	public static Image img(String path) {
		return getImageDescriptor(path).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin("", path);
	}
	
    public static ImageDescriptor imageDescriptorFromPlugin(String pluginId,
            String imageFilePath) {
    	return null;
    }

}
