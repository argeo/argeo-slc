package org.argeo.slc.client.oxm;

public interface OxmInterface {

	public String marshal(Object graph);

	public Object unmarshal(String result);

}
