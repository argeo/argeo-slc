package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.context.SimpleContextAware;
import org.argeo.slc.test.context.ContextAware;

/**
 * Overrides Values and Expected values of a target 
 * <code>SimpleContextAware</code> with the corresponding
 * values and expected values of a source <code>ContextAware</code>
 *
 */
public class OverrideContextAware implements Runnable {

	private ContextAware source;

	private SimpleContextAware target;
	
	/**
	 * Whether an exception shall be thrown if a value
	 * or expected value of the source is not defined
	 * in the target
	 */
	private Boolean failIfUndefinedInSource = true;
	
	public void run() {
		// override values
		if(source.getValues() != null)
			for(String key : source.getValues().keySet()) {
				if(failIfUndefinedInSource && !target.getValues().containsKey(key)) {
					throw new SlcException("No entry in target values for key '" + key + "'");
				}
				target.getValues().put(key, source.getValues().get(key));
			}
		
		// override expected values
		if(source.getExpectedValues() != null)
			for(String key : source.getExpectedValues().keySet()) {
				if(failIfUndefinedInSource && !target.getExpectedValues().containsKey(key)) {
					throw new SlcException("No entry in target expected values for key '" + key + "'");
				}
				target.getExpectedValues().put(key, source.getExpectedValues().get(key));
			}		
	}	
	
	public void setSource(ContextAware source) {
		this.source = source;
	}

	public void setTarget(SimpleContextAware target) {
		this.target = target;
	}

	public void setFailIfUndefinedInSource(Boolean failIfUndefinedInSource) {
		this.failIfUndefinedInSource = failIfUndefinedInSource;
	}	
}
