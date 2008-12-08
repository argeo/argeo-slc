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

	protected StringResult marshalAndValidate(Object obj) throws Exception {
		StringResult xml = new StringResult();
		marshaller.marshal(obj, xml);

		log.info("Marshalled ResultPart Request: " + xml);

		UnitXmlUtils.assertXmlValidation(getBean(XmlValidator.class),
				new StringSource(xml.toString()));
		return xml;
	}

	@SuppressWarnings("unchecked")
	protected <T> T unmarshal(StringResult xml) throws Exception {
		return (T) unmarshaller.unmarshal(new StringSource(xml.toString()));
	}

	@SuppressWarnings("unchecked")
	protected <T> T marshUnmarsh(Object obj) throws Exception {
		StringResult xml = marshalAndValidate(obj);
		return (T)unmarshal(xml);
	}
}
