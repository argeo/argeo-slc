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
package org.argeo.slc.web.mvc;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.process.SlcExecution;
import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.view.xslt.XsltView;
import org.w3c.dom.Document;

public class XsltMarshallerView extends XsltView {

	private Marshaller marshaller;

	@Override
	protected Class<?>[] getSourceTypes() {
		return new Class[] { TreeTestResult.class,
				TreeTestResultCollection.class, SlcExecution.class };
	}

	@Override
	protected Source convertSource(Object source) throws Exception {
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		DOMResult result = new DOMResult(document);
		if (!marshaller.supports(source.getClass()))
			throw new SlcException("Object of type " + source.getClass()
					+ " not supported.");
		marshaller.marshal(source, result);
		return new DOMSource(result.getNode());
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

}
