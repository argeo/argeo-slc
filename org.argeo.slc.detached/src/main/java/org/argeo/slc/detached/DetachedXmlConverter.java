package org.argeo.slc.detached;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

public interface DetachedXmlConverter {
	public void marshallRequest(DetachedRequest request, Result result);

	public DetachedRequest unmarshallRequest(Source source);

	public void marshallAnswer(DetachedAnswer answer, Result result);

	public DetachedAnswer unmarshallAnswer(Source source);
}
