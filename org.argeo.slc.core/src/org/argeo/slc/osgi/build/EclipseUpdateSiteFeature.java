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
package org.argeo.slc.osgi.build;

import java.util.ArrayList;
import java.util.List;

public class EclipseUpdateSiteFeature {
	private String name;
	private List<EclipseUpdateSiteCategory> categories = new ArrayList<EclipseUpdateSiteCategory>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EclipseUpdateSiteCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<EclipseUpdateSiteCategory> categories) {
		this.categories = categories;
	}

}
