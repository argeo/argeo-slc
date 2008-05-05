package org.argeo.slc.web.mvc;

import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;

public class XsltMarshallerViewResolver extends XsltViewResolver {

	private Marshaller marshaller;
	
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

	
}
