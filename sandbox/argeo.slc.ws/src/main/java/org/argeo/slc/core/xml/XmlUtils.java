package org.argeo.slc.core.xml;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import org.apache.commons.io.IOUtils;

public class XmlUtils {
	private static TransformerFactory transformerFactory = TransformerFactory
			.newInstance();

	public static String getDomAsString(Document doc, boolean indent)
			throws IOException, TransformerConfigurationException,
			TransformerException {
		StringWriter out = new StringWriter();
		try {
			// Create identity transformer
			Transformer transformer = transformerFactory.newTransformer();
			if (indent) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			}
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
			return out.toString();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
