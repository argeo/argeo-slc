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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.SlcException;
import org.springframework.web.servlet.view.AbstractView;

import com.springsource.json.writer.JSONObject;

/** Marshal one of the object of the map to the output. */
public class JsonView extends AbstractView {
	private String modelKey = null;

	public JsonView() {
	}

	@Override
	@SuppressWarnings(value = { "unchecked" })
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final Object answer;
		if (modelKey != null) {
			if (!model.containsKey(modelKey))
				throw new SlcException("Key " + modelKey
						+ " not found in model.");
			answer = model.get(modelKey);
		} else {
			if (model.size() != 1)
				throw new SlcException(
						"Model has a size different from 1. Specify a modelKey.");
			answer = model.values().iterator().next();
		}

		if (answer instanceof JSONObject) {
			((JSONObject) answer).write(response.getWriter());
		} else {
			JSONObject jsonObject = new JSONObject(answer);
			jsonObject.write(response.getWriter());
		}
	}

	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

}
