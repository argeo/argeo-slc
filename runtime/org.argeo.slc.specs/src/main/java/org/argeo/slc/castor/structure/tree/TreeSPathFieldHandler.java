package org.argeo.slc.castor.structure.tree;

import org.exolab.castor.mapping.AbstractFieldHandler;
import org.exolab.castor.mapping.MapItem;

import org.argeo.slc.core.structure.tree.TreeSPath;

public class TreeSPathFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		MapItem part = (MapItem) object;
		return ((TreeSPath) part.getKey()).getAsUniqueString();
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
		MapItem part = (MapItem) object;
		part.setKey(null);
	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		MapItem part = (MapItem) object;
		part.setKey(TreeSPath.parseToCreatePath(value.toString()));
	}

}
