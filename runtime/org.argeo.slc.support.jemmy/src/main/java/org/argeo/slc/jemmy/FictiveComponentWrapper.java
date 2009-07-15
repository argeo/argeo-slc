package org.argeo.slc.jemmy;

import java.util.HashMap;
import java.util.Map;

import org.netbeans.jemmy.operators.ComponentOperator;

public class FictiveComponentWrapper implements ComponentWrapper {

	protected ComponentWrapper parent;	
	
	public ComponentOperator find() {
		// just ask the parent
		return parent.find();
	}

	/**
	 * Return only itself (if the class matches)
	 */
	public Map getAccessors(Class accessorClass) {
		Map accessors = new HashMap();
		if (accessorClass.isInstance(this)) {
			accessors.put(((Accessor) this).getFieldName(), this);
		}		
		return accessors;
	}

	public ComponentWrapper getParent() {
		return parent;
	}

	public void setParent(ComponentWrapper parent) {
		this.parent = parent;
	}

}
