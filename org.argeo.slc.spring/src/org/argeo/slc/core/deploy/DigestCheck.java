/*
 * Copyright (C) 2007-2012 Argeo GmbH
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

import java.io.File;
import java.io.IOException;

import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;

/** Add Spring capabilities to {@link DigestUtils} */
public class DigestCheck extends DigestUtils {
	public static String digest(String algorithm, Resource resource) {
		try {
			File file = resource.getFile();
			return org.argeo.util.DigestUtils.digest(algorithm, file);
		} catch (IOException e) {
			try {
				return org.argeo.util.DigestUtils.digest(algorithm,
						resource.getInputStream());
			} catch (IOException e1) {
				throw new SlcException("Cannot digest " + resource
						+ " with algorithm " + algorithm, e);
			}
		}
	}
}
