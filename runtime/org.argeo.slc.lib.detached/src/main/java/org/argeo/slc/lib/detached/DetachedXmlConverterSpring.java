/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.lib.detached;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.argeo.slc.SlcException;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedCommunication;
import org.argeo.slc.detached.DetachedException;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedXmlConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

public class DetachedXmlConverterSpring implements DetachedXmlConverter {
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public void marshallCommunication(DetachedCommunication detCom,
			Result result) {
		if (detCom instanceof DetachedRequest) {
			marshallRequest((DetachedRequest) detCom, result);
		} else if (detCom instanceof DetachedAnswer) {
			marshallAnswer((DetachedAnswer) detCom, result);
		} else {
			throw new DetachedException("Unkown communication type "
					+ detCom.getClass());
		}
	}

	public DetachedCommunication unmarshallCommunication(Source source) {
		try {
			return (DetachedCommunication) unmarshaller.unmarshal(source);
		} catch (Exception e) {
			throw new SlcException("Could not unmarshall", e);
		}
	}

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

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

}
