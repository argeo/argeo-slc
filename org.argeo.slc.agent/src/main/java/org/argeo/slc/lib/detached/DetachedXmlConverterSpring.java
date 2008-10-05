package org.argeo.slc.lib.detached;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedXmlConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.xml.validation.XmlValidator;
import org.xml.sax.InputSource;

public class DetachedXmlConverterSpring implements DetachedXmlConverter {
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public void marshallRequest(DetachedRequest request, Result result) {
		try {
			marshaller.marshal(request, result);
		} catch (Exception e) {
			throw new SlcException("Could not marshall", e);
		}
	}

	public void marshallAnswer(DetachedAnswer answer, Result result) {
		try {
			marshaller.marshal(answer, result);
		} catch (Exception e) {
			throw new SlcException("Could not marshall", e);
		}
	}

	public DetachedAnswer unmarshallAnswer(Source source) {
		try {
			return (DetachedAnswer) unmarshaller.unmarshal(source);
		} catch (Exception e) {
			throw new SlcException("Could not unmarshall", e);
		}
	}

	public DetachedRequest unmarshallRequest(Source source) {
		try {
			return (DetachedRequest) unmarshaller.unmarshal(source);
		} catch (Exception e) {
			throw new SlcException("Could not unmarshall", e);
		}
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

}
