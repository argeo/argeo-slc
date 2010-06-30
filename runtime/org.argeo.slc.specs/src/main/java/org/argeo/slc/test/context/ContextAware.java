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

package org.argeo.slc.test.context;

import java.util.Map;

public interface ContextAware {
	public final static String DEFAULT_SKIP_FLAG = "!";
	public final static String DEFAULT_ANY_FLAG = "*";

	public Map<String, Object> getValues();

	public void setValues(Map<String, Object> values);

	public Map<String, Object> getExpectedValues();

	public String getContextSkipFlag();

	public String getContextAnyFlag();
}
