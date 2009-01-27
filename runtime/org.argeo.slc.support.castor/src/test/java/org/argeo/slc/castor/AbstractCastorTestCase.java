package org.argeo.slc.castor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.UnitXmlUtils;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.validation.XmlValidator;

public abstract class AbstractCastorTestCase extends AbstractSpringTestCase {
	protected Log log = LogFactory.getLog(getClass());

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	@Override
	public void setUp() {
		marshaller = getBean(Marshaller.class);
		unmarshaller = getBean(Unmarshaller.class);
	}

	protected StringResult marshal(Object obj) throws Exception {
		return marshal(obj, false);
	}

	protected StringResult marshalAndValidate(Object obj) throws Exception {
		return marshal(obj, true);
	}

	protected StringResult marshal(Object obj, boolean validate)
			throws Exception {
		StringResult xml = new StringResult();
		marshaller.marshal(obj, xml);

		log.info("Marshalled ResultPart Request: " + xml);

		if (validate)
			UnitXmlUtils.assertXmlValidation(getBean(XmlValidator.class),
					new StringSource(xml.toString()));
		return xml;
	}

	@SuppressWarnings("unchecked")
	protected <T> T unmarshal(StringResult xml) throws Exception {
		return (T) unmarshaller.unmarshal(new StringSource(xml.toString()));
	}

	@SuppressWarnings("unchecked")
	protected <T> T marshUnmarsh(Object obj, boolean validate) throws Exception {
		StringResult xml = marshal(obj, validate);
		return (T) unmarshal(xml);
	}

	@SuppressWarnings("unchecked")
	protected <T> T marshUnmarsh(Object obj) throws Exception {
		return (T) marshUnmarsh(obj, true);
	}
}
