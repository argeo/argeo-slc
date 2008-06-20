package org.argeo.slc.spring;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class SpringUtils {
	private final static Log log = LogFactory.getLog(SpringUtils.class);

	public static <T> T loadSingleFromContext(ListableBeanFactory context,
			Class<T> clss) {
		Map<String, T> beans = context.getBeansOfType(clss);
		if (beans.size() == 1) {
			return beans.values().iterator().next();
		} else {
			return null;
		}
	}

	public static Resource getParent(Resource res) {
		try {
			if (res.getURL().getPath().equals("/"))
				return null;

			String urlStr = res.getURL().toString();
			if (urlStr.charAt(urlStr.length() - 1) == '/')
				urlStr = urlStr.substring(0, urlStr.length() - 2);

			String parentUrlStr = urlStr.substring(0, urlStr.lastIndexOf('/'));
			return new DefaultResourceLoader(Thread.currentThread()
					.getContextClassLoader()).getResource(parentUrlStr + '/');
		} catch (IOException e) {
			throw new SlcException("Cannot get parent for resource " + res, e);
		}
	}

	public static String extractRelativePath(Resource ancestor, Resource child) {
		try {
			String childPath = child.getURL().getPath();
			String ancestorPath = ancestor.getURL().getPath();

			if (log.isTraceEnabled())
				log.trace("extractRelativePath(): childPath=" + childPath
						+ ", ancestorPath=" + ancestorPath);

			return childPath.substring(ancestorPath.length());
		} catch (IOException e) {
			throw new SlcException("Cannot extract relative path of " + child
					+ " based on " + ancestor, e);
		}
	}

	private SpringUtils() {

	}
}
