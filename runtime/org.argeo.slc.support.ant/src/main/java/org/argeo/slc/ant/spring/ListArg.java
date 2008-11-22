package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.SlcException;

/** List of overrides */
public class ListArg {
	private List<OverrideArg> list = new Vector<OverrideArg>();

	/** Creates override sub tag. */
	public OverrideArg createOverride() {
		OverrideArg overrideArg = new OverrideArg();
		list.add(overrideArg);
		return overrideArg;
	}

	/** Gets as list of objects. */
	public List<Object> getAsObjectList(List<Object> originalList) {
		if (originalList != null && originalList.size() != list.size()) {
			throw new SlcException("Cannot merge lists of different sizes.");
		}

		List<Object> objectList = new Vector<Object>(list.size());

		for (int i = 0; i < list.size(); i++) {
			OverrideArg arg = list.get(i);

			if (originalList != null)
				arg.setOriginal(originalList.get(i));

			objectList.add(arg.getObject());
		}
		return objectList;
	}
}
