package org.argeo.slc.core.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class RelativeResourceSet implements ResourceLoaderAware,
		InitializingBean {
	private final static Log log = LogFactory.getLog(RelativeResourceSet.class);
	public final static String DEFAULT_EXCLUDES = "**/.svn/**";

	private String base;
	private String include;
	private List<String> includes = new ArrayList<String>();
	private String exclude;
	private List<String> excludes = new ArrayList<String>();
	private Boolean useDefaultExcludes = true;
	private ResourcePatternResolver resourcePatternResolver;
	private PathMatcher excludePathMatcher = new AntPathMatcher();

	private ResourceLoader resourceLoader;

	/** List the resources, identified by their relative path. */
	public Map<String, Resource> listResources() {
		try {
			String baseResUrl = resourceLoader.getResource(base).getURL()
					.toString();
			Map<String, Resource> res = new TreeMap<String, Resource>();
			for (String includePattern : includes)
				processInclude(res, includePattern, baseResUrl);
			return res;
		} catch (IOException e) {
			throw new SlcException("Cannot list resource from " + base, e);
		}
	}

	protected void processInclude(Map<String, Resource> res, String include,
			String baseResUrl) throws IOException {
		String pattern = base + "/" + include;
		if (log.isDebugEnabled())
			log.debug("Look for resources with pattern '" + pattern + "'");
		Resource[] resources = resourcePatternResolver.getResources(pattern);
		resources: for (Resource resource : resources) {
			String url = resource.getURL().toString();
			String relPath = url.substring(baseResUrl.length());

			// skip dir
			if (relPath.charAt(relPath.length() - 1) == '/')
				continue resources;

			// make sure there is not starting '/'
			if (relPath.charAt(0) == '/')
				relPath = relPath.substring(1);

			// skip excludes
			for (String exclude : excludes)
				if (excludePathMatcher.match(exclude, relPath))
					continue resources;

			// check if already exists
			if (res.containsKey(relPath))
				log.warn(relPath + " already matched by " + res.get(relPath)
						+ ", " + resource + " will override it.");

			// store the marched resource
			res.put(relPath, resource);
			if (log.isDebugEnabled())
				log.debug(relPath + "=" + resource);
		}

	}

	public void afterPropertiesSet() throws Exception {
		if (resourcePatternResolver == null)
			resourcePatternResolver = new PathMatchingResourcePatternResolver(
					resourceLoader);
		if (include != null)
			addCommaSeparatedToList(include, includes);
		if (exclude != null)
			addCommaSeparatedToList(exclude, excludes);

		if (includes.size() == 0)
			includes.add("**");

		if (useDefaultExcludes)
			addCommaSeparatedToList(DEFAULT_EXCLUDES, excludes);
	}

	private void addCommaSeparatedToList(String str, List<String> lst) {
		StringTokenizer st = new StringTokenizer(str, ",");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!lst.contains(token))
				lst.add(token);
		}
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setUseDefaultExcludes(Boolean useDefaultExcludes) {
		this.useDefaultExcludes = useDefaultExcludes;
	}

	public void setExcludePathMatcher(PathMatcher excludePathMatcher) {
		this.excludePathMatcher = excludePathMatcher;
	}

	public void setResourcePatternResolver(
			ResourcePatternResolver resourcePatternResolver) {
		this.resourcePatternResolver = resourcePatternResolver;
	}

	public ResourcePatternResolver getResourcePatternResolver() {
		return resourcePatternResolver;
	}

}
