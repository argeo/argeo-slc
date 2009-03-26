package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.context.SimpleContextAware;
import org.argeo.slc.core.test.context.SimpleParentContextAware;
import org.argeo.slc.execution.Executable;
import org.argeo.slc.test.context.ContextAware;

public class OverrideContextAwareTask implements Executable {

	private ContextAware source;

	private SimpleContextAware target;
	

	public void setSource(ContextAware source) {
		this.source = source;
	}


	public void setTarget(SimpleContextAware target) {
		this.target = target;
	}


	public void execute() {
		// override values
		if(source.getValues() != null)
			for(String key : source.getValues().keySet()) {
				if(!target.getValues().containsKey(key)) {
					throw new SlcException("No entry in target values for key '" + key + "'");
				}
				target.getValues().put(key, source.getValues().get(key));
			}
		
		// override expected values
		if(source.getExpectedValues() != null)
			for(String key : source.getExpectedValues().keySet()) {
				if(!target.getExpectedValues().containsKey(key)) {
					throw new SlcException("No entry in target expected values for key '" + key + "'");
				}
				target.getExpectedValues().put(key, source.getExpectedValues().get(key));
			}		
	}	
}
