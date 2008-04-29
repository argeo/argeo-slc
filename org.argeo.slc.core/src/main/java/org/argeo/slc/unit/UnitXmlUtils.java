package org.argeo.slc.unit;

import java.io.IOException;

import javax.xml.transform.Source;

import junit.framework.TestCase;

import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.xsd.XsdSchema;
import org.xml.sax.SAXParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class UnitXmlUtils {
	private final static Log log = LogFactory.getLog(UnitXmlUtils.class);

	public static void assertXsdSchemaValidation(XsdSchema schema, Source source)
			throws IOException {
		XmlValidator validator = schema.createValidator();
		SAXParseException[] exceptions = validator.validate(source);
		if (exceptions.length != 0) {
			for (SAXParseException ex : exceptions) {
				log.error(ex.getMessage());
			}
			TestCase.fail("Could not validate with schema " + schema);
		}
	}

	private UnitXmlUtils() {

	}
}
