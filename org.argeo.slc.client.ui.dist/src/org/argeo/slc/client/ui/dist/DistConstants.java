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
package org.argeo.slc.client.ui.dist;

import org.argeo.slc.jcr.SlcNames;
import org.osgi.framework.Constants;

/** Constants used across the application. */
public interface DistConstants {

	/*
	 * MISCEALLENEOUS
	 */
	public final static String DATE_TIME_FORMAT = "MM/dd/yyyy, HH:mm";
	public final static String DATE_FORMAT = "MM/dd/yyyy";
	// this should be directly retrieved from JCR APIs once we have solved the
	// problem of the translation of name space to shortcut, typically
	// {http://www.jcp.org/jcr/1.0} to jcr:
	public final static String JCR_IDENTIFIER = "jcr:uuid";
	public final static String JCR_MIXIN_TYPES = "jcr:mixinTypes";

	// FIXME: should be defined in SlcNames
	public final static String SLC_BUNDLE_NAME = SlcNames.SLC_
			+ Constants.BUNDLE_NAME;
	public final static String SLC_BUNDLE_LICENCE = SlcNames.SLC_
			+ "Bundle-License";
	public final static String SLC_BUNDLE_VENDOR = SlcNames.SLC_
			+ Constants.BUNDLE_VENDOR;

	public final static String SLC_BUNDLE_DESCRIPTION = SlcNames.SLC_
			+ Constants.BUNDLE_DESCRIPTION;

	public final String DEFAULT_PUBLIC_REPOSITORY_URI = "vm:///java";

}