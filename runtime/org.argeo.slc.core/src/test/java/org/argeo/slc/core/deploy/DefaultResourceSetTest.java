/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.core.deploy;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.context.AbstractInternalSpringTestCase;
import org.springframework.core.io.Resource;

public class DefaultResourceSetTest extends AbstractInternalSpringTestCase {
	private final static Log log = LogFactory
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
