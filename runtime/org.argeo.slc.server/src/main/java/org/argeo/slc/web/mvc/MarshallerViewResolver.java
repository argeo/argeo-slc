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

package org.argeo.slc.web.mvc;

import java.util.Locale;

import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

/**
 * Returns a MarshallerView based on the underlying marshaller. View name is the
 * model key of the marshaller view.
 */
public class MarshallerViewResolver extends AbstractCachingViewResolver {
	private final Marshaller marshaller;

	public MarshallerViewResolver(Marshaller marshaller) {
		super();
		this.marshaller = marshaller;
	}

	/**
	 * Caches a marshaller view.
	 * 
	 * @param viewName
	 *            can be null, default marshaller view behavior is then used
	 */
	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		return new MarshallerView(marshaller, viewName);
	}

}
