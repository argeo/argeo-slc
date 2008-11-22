package org.argeo.slc.detached;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

public interface DetachedXmlConverter {
	public void marshallCommunication(DetachedCommunication detCom,
			Result result);

	public DetachedCommunication unmarshallCommunication(Source source);
}
