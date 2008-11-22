package org.argeo.slc.web.mvc;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
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
