package org.argeo.slc.web.mvc;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.view.xslt.XsltView;
import org.w3c.dom.Document;

import org.argeo.slc.core.test.tree.TreeTestResult;

public class XsltMarshallerView extends XsltView {

	private Marshaller marshaller;
	
	

	@Override
	protected Class<?>[] getSourceTypes() {
		return new Class[]{TreeTestResult.class};
	}

	@Override
	protected Source convertSource(Object source) throws Exception {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		DOMResult result = new DOMResult(document);
		marshaller.marshal(source, result);
		return new DOMSource(result.getNode());
	}



	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	
}
