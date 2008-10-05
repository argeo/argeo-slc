package org.argeo.slc.detached;

import java.io.StringReader;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** For compatibility with old JDKs (pre 1.5). Use Spring base one when possible. */
public class DetachedXmlConverterCompat implements DetachedXmlConverter {
	public final static String SLC_DETACHED_NAMESPACE_URI = "http://argeo.org/projects/slc/schemas/detached";
	// private final static Log log = LogFactory
	// .getLog(DetachedXmlConverterCompat.class);

	private final Transformer copy;

	public DetachedXmlConverterCompat() {
		try {
			copy = TransformerFactory.newInstance().newTransformer();
		} catch (Exception e) {
			throw new DetachedException("Could not create transformer.", e);
		}
	}

	public void marshallAnswer(DetachedAnswer answer, Result result) {
		StringBuffer buf = new StringBuffer("");
		buf.append("<slc-det:detached-answer xmlns:slc-det=\""
				+ SLC_DETACHED_NAMESPACE_URI + "\">");
		if (answer.getUuid() != null)
			buf.append("<slc-det:uuid>").append(answer.getUuid()).append(
					"</slc-det:uuid>");
		buf.append("<slc-det:status>").append(answer.getStatus()).append(
				"</slc-det:status>");
		if (answer.getLog() != null)
			buf.append("<slc-det:log>").append(answer.getLog()).append(
					"</slc-det:log>");
		if (answer.getProperties().size() != 0) {
			buf.append("<slc-det:properties>");
			Enumeration keys = answer.getProperties().keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				buf.append("<slc-det:prop key=\"").append(key).append("\">");
				buf.append(answer.getProperties().getProperty(key));
				buf.append("</slc-det:prop>");
			}
			buf.append("</slc-det:properties>");
		}
		buf.append("</slc-det:detached-answer>");
		StringReader reader = new StringReader(buf.toString());
		Source source = new StreamSource(reader);
		try {
			copy.transform(source, result);
		} catch (TransformerException e) {
			throw new DetachedException("Could not copy xml to result", e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public void marshallRequest(DetachedRequest request, Result result) {
		throw new DetachedException("Not implemented.");
	}

	public DetachedAnswer unmarshallAnswer(Source source) {
		throw new DetachedException("Not implemented.");
	}

	public DetachedRequest unmarshallRequest(Source source) {
		DOMResult result = new DOMResult();
		try {
			copy.transform(source, result);
		} catch (TransformerException e) {
			throw new DetachedException("Could not copy xml source", e);
		}
		Element node = (Element) ((Document) result.getNode())
				.getDocumentElement();
		DetachedRequest request = new DetachedRequest();
		request.setUuid(node.getElementsByTagNameNS(SLC_DETACHED_NAMESPACE_URI,
				"uuid").item(0).getTextContent());
		request.setRef(node.getElementsByTagNameNS(SLC_DETACHED_NAMESPACE_URI,
				"ref").item(0).getTextContent());
		request.setPath(node.getElementsByTagNameNS(SLC_DETACHED_NAMESPACE_URI,
				"path").item(0).getTextContent());
		Element propertiesElement = (Element) node.getElementsByTagNameNS(
				SLC_DETACHED_NAMESPACE_URI, "properties").item(0);
		NodeList propElements = propertiesElement.getElementsByTagNameNS(
				SLC_DETACHED_NAMESPACE_URI, "prop");
		Properties properties = new Properties();
		for (int i = 0; i < propElements.getLength(); i++) {
			Element propElement = (Element) propElements.item(i);
			String key = propElement.getAttribute("key");
			String value = propElement.getTextContent();
			properties.setProperty(key, value);
		}
		request.setProperties(properties);
		return request;
	}
}
