/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		assertXmlValidation(validator, source);
	}

	public static void assertXmlValidation(XmlValidator validator, Source source)
			throws IOException {
		SAXParseException[] exceptions = validator.validate(source);
		if (exceptions.length != 0) {
			for (SAXParseException ex : exceptions) {
				log.error(ex.getMessage());
			}
			TestCase.fail("Could not validate");
		}
	}

	private UnitXmlUtils() {

	}
}
