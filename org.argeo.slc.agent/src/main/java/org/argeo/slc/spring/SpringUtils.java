package org.argeo.slc.spring;

import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;

public class SpringUtils {
	public static <T> T loadSingleFromContext(ListableBeanFactory context,
			Class<T> clss) {
		Map<String, T> beans = context.getBeansOfType(clss);
		if (beans.size() == 1) {
			return beans.values().iterator().next();
		} else {
			return null;
		}
	}

	private SpringUtils() {

	}
}
