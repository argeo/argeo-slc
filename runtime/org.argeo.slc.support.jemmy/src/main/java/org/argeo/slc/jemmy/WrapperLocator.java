package org.argeo.slc.jemmy;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface WrapperLocator {

	public ComponentOperator find(ComponentWrapper wrapper);
}
