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

package org.argeo.slc.castor.test.tree;

import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.SlcTestUtils;
import org.argeo.slc.test.TestStatus;
import org.exolab.castor.mapping.AbstractFieldHandler;

public class StatusFieldHandler extends AbstractFieldHandler {

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		SimpleResultPart part = (SimpleResultPart) object;
		return SlcTestUtils.statusToString(part.getStatus());
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		return null;
	}

	@Override
	public Object newInstance(Object parent, Object[] args)
			throws IllegalStateException {
		return null;
	}

	@Override
	public void resetValue(Object object) throws IllegalStateException,
			IllegalArgumentException {
		SimpleResultPart part = (SimpleResultPart) object;
		// ERROR by default since it should be explicitely set
		part.setStatus(TestStatus.ERROR);
	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		SimpleResultPart part = (SimpleResultPart) object;
		Integer status = SlcTestUtils.stringToStatus((String) value);
		part.setStatus(status);
	}

}
