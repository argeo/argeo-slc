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
package org.argeo.slc.web.ajaxplorer;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleAjxpDriver implements AjxpDriver {
	protected final Log log = LogFactory.getLog(getClass());
	private Map<String, AjxpAction<? extends AjxpDriver>> actions = new TreeMap<String, AjxpAction<? extends AjxpDriver>>();

	public AjxpAnswer executeAction(HttpServletRequest request) {
		String actionStr = request.getParameter("get_action");
		if (actionStr == null) {
			actionStr = request.getParameter("action");
		}
		if (!actions.containsKey(actionStr)) {
			throw new AjxpDriverException("Action " + actionStr
					+ " not defined.");
		}
		AjxpAction action = actions.get(actionStr);
		return action.execute(this,request);
	}

	public void setActions(Map<String, AjxpAction<? extends AjxpDriver>> actions) {
		this.actions = actions;
	}

}
