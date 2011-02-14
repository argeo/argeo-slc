package org.argeo.slc.client.oxm;

import org.argeo.slc.SlcException;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

public class OxmBean implements OxmInterface {

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public void init() {
	}

	public Object unmarshal(String result) {
		Object res;
		if (result == null)
			throw new SlcException("Cannot unmarshall empty string ");
		try {
			res = unmarshaller.unmarshal(new StringSource(result));
		} catch (Exception e) {
			throw new SlcException("Could not unmarshall " + result, e);
		}
		return res;
	}

	public String marshal(Object graph) {
		StringResult result = new StringResult();
		try {
			marshaller.marshal(graph, result);
		} catch (Exception e) {
			throw new SlcException("Cannot Marshal object " + graph.toString()
					+ " - " + e);
		}
		return result.toString();
	}

	// IoC
	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
}
