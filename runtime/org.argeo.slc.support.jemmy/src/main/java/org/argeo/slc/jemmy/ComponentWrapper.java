package org.argeo.slc.jemmy;

import java.util.Map;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface ComponentWrapper {

	/**
	 * Finds the component described by the ComponentWrapper
	 * in the GUI
	 * @return a Jemmy ComponentOperator for the found component
	 */
	public ComponentOperator find();
	
	public void setParent(ComponentWrapper parent);
	
	public ComponentWrapper getParent();
	
	public Map getAccessors(Class accessorClass);
}
