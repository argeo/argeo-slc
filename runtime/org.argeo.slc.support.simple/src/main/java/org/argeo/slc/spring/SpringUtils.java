package org.argeo.slc.spring;

import java.net.URI;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class SpringUtils {
	private final static Log log = LogFactory.getLog(SpringUtils.class);

	@SuppressWarnings(value = { "unchecked" })
	public static <T> T loadSingleFromContext(ListableBeanFactory context,
			Class<T> clss) {
		// Map<String, T> beans = context.getBeansOfType(clss);
		Map<String, T> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				context, clss, false, false);
		if (beans.size() == 1) {
			return beans.values().iterator().next();
		} else if (beans.size() > 1) {
			if (log.isDebugEnabled()) {
				log
						.debug(("Found more that on bean for type " + clss
								+ ": " + beans.keySet()));
			}
			return null;
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

			String parentUrlStr = urlStr.substring(0, urlStr.lastIndexOf('/')) + '/';
			URI uri = new URI(parentUrlStr).normalize();
			return new DefaultResourceLoader(Thread.currentThread()
					.getContextClassLoader()).getResource(uri.toString());
		} catch (Exception e) {
			throw new SlcException("Cannot get parent for resource " + res, e);
		}
	}

	public static String extractRelativePath(Resource ancestor, Resource child) {
		try {

			return ancestor.getURL().toURI().relativize(child.getURL().toURI())
					.normalize().toString();
		} catch (Exception e) {
			throw new SlcException("Cannot extract relative path of " + child
					+ " based on " + ancestor, e);
		}
	}

	private SpringUtils() {

	}
}
