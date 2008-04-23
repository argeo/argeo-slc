package org.argeo.slc.spring;

import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;

public class SpringUtils {
	public static <T> T loadSingleFromContext(ListableBeanFactory context,
			Class<T> clss) {
		Map<String, T> listeners = context.getBeansOfType(clss);
		if (listeners.size() == 1) {
			return listeners.values().iterator().next();
		} else {
			return null;
		}
	}

	private SpringUtils() {

	}
}
