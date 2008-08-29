package org.argeo.slc.web.mvc;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;

public class XsltMarshallerViewResolver extends XsltViewResolver implements
		URIResolver {
	private final static Log log = LogFactory
			.getLog(XsltMarshallerViewResolver.class);

	private Marshaller marshaller;
	private Map<String, Source> cacheUriResolver = new TreeMap<String, Source>();

	public XsltMarshallerViewResolver() {
		setUriResolver(this);
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		AbstractUrlBasedView viewT = super.buildView(viewName);
		XsltMarshallerView view = (XsltMarshallerView) viewT;
		view.setMarshaller(marshaller);
		return view;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Source resolve(String href, String base) throws TransformerException {
		if (log.isTraceEnabled())
			log.trace("Resolve URI for href=" + href + " base=" + base);

		Source res = null;
		if (isCache())
			res = cacheUriResolver.get(href);

		if (res == null)
			res = getStylesheetSource(href);

		if (res == null)
			res = getStylesheetSource(getPrefix() + href);

		if (res == null)
			throw new SlcException("Can't resolve URI for href=" + href
					+ " base=" + base);

		if (isCache() && !cacheUriResolver.containsKey(href))
			cacheUriResolver.put(href, res);

		return res;
	}

	protected Source getStylesheetSource(String url) {
		if (log.isDebugEnabled()) {
			log.debug("Loading XSLT stylesheet from '" + url + "'");
		}
		try {
			final Resource stylesheetResource = getApplicationContext()
					.getResource(url);
			String systemId = url.substring(0, url.lastIndexOf('/') + 1);
			return new StreamSource(stylesheetResource.getInputStream(),
					systemId);
		} catch (IOException e) {
			if (log.isTraceEnabled())
				log.trace("Cannot load stylesheet " + url, e);
			return null;
		}
	}

}
