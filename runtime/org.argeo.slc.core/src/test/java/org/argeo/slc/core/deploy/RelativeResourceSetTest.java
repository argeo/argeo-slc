package org.argeo.slc.core.deploy;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.context.AbstractInternalSpringTestCase;
import org.springframework.core.io.Resource;

public class RelativeResourceSetTest extends AbstractInternalSpringTestCase {
	private final static Log log = LogFactory
			.getLog(RelativeResourceSetTest.class);

	public void testListResources() {
		RelativeResourceSet rrs = getBean("relativeResourceSet");
		Map<String, Resource> res = rrs.listResources();
		for (String relativePath : res.keySet())
			log.debug(relativePath + "=" + res.get(relativePath));
		assertEquals(2, res.size());
	}

	@Override
	protected String getApplicationContextLocation() {
		return inPackage("relativeResourceSet.xml");
	}

}
