package org.argeo.slc.castor.test.tree;

import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.SlcTestUtils;
import org.argeo.slc.test.TestStatus;
import org.exolab.castor.mapping.AbstractFieldHandler;

public class StatusFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		SimpleResultPart part = (SimpleResultPart) object;
		return SlcTestUtils.statusToString(part.getStatus());
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		return null;
	}

	@Override
	public Object newInstance(Object parent, Object[] args)
			throws IllegalStateException {
		return null;
	}

	@Override
	public void resetValue(Object object) throws IllegalStateException,
			IllegalArgumentException {
		SimpleResultPart part = (SimpleResultPart) object;
		// ERROR by default since it should be explicitely set
		part.setStatus(TestStatus.ERROR);
	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		SimpleResultPart part = (SimpleResultPart) object;
		Integer status = SlcTestUtils.stringToStatus((String) value);
		part.setStatus(status);
	}

}
