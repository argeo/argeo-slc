package org.argeo.slc.core.deploy;

import java.util.Map;

import org.argeo.api.cms.CmsLog;
import org.argeo.slc.core.test.context.AbstractInternalSpringTestCase;
import org.springframework.core.io.Resource;

public class DefaultResourceSetTest extends AbstractInternalSpringTestCase {
	private final static CmsLog log = CmsLog
			.getLog(DefaultResourceSetTest.class);

	public void testListResources() {
		DefaultResourceSet rrs = getBean("relativeResourceSet");
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
