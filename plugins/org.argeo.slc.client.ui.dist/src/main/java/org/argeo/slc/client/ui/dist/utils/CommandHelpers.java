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
package org.argeo.slc.client.ui.dist.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Centralizes useful methods to manage command updates
 */
public class CommandHelpers {

	/**
	 * Refresh the given command. 
	 */
	public static void refreshCommand(IMenuManager menuManager,
			IServiceLocator locator, String cmdId, String label, String iconPath,
			boolean showCommand) {
		IContributionItem ici = menuManager.find(cmdId);
		if (ici != null)
			menuManager.remove(ici);

		if (showCommand) {
			// Set Params
			CommandContributionItemParameter contributionItemParameter = new CommandContributionItemParameter(
					locator, null, cmdId, SWT.PUSH);
			contributionItemParameter.label = label;
			contributionItemParameter.icon = DistPlugin.getImageDescriptor(iconPath);
			CommandContributionItem cci = new CommandContributionItem(
					contributionItemParameter);
			cci.setId(cmdId);
			menuManager.add(cci);
		}
	}

	/**
	 * Refresh the given command and optionally corresponding parameters.
	 * 
	 * @param menuManager
	 * @param locator
	 * @param cmdId
	 * @param label
	 * @param showCommand
	 *            Command must be explicitly removed from the context menu at
	 *            each refresh setting this to false.
	 * @param params
	 *            maps a paramId with a String value
	 */
	public static void refreshParameterizedCommand(IMenuManager menuManager,
			IServiceLocator locator, String cmdId, String label, String iconPath,
			boolean showCommand, Map<String, String> params) {
		IContributionItem ici = menuManager.find(cmdId);
		if (ici != null)
			menuManager.remove(ici);
		
		if (showCommand) {
			// Set Params
			CommandContributionItemParameter contributionItemParameter = new CommandContributionItemParameter(
					locator, null, cmdId, SWT.PUSH);
			contributionItemParameter.label = label;
			contributionItemParameter.icon = DistPlugin.getImageDescriptor(iconPath);

			if (params != null)
				contributionItemParameter.parameters = params;

			CommandContributionItem cci = new CommandContributionItem(
					contributionItemParameter);
			cci.setId(cmdId);
			menuManager.add(cci);
		}
	}

	/** Helper to call a command without parameter easily */
	public static void callCommand(String commandID) {
		callCommand(commandID, null);
	}

	/** Helper to call a command with a single parameter easily */
	public static void callCommand(String commandID, String parameterID,
			String parameterValue) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(parameterID, parameterValue);
		callCommand(commandID, params);
	}

	/**
	 * Helper to call a command with a map of parameters easily
	 * 
	 * @param paramMap
	 *            a map that links various commands ids with corresponding
	 *            String values.
	 */
	public static void callCommand(String commandID,
			Map<String, String> paramMap) {
		try {
			IWorkbench iw = DistPlugin.getDefault().getWorkbench();
			IHandlerService handlerService = (IHandlerService) iw
					.getService(IHandlerService.class);
			ICommandService cmdService = (ICommandService) iw
					.getActiveWorkbenchWindow().getService(
							ICommandService.class);
			Command cmd = cmdService.getCommand(commandID);

			ArrayList<Parameterization> parameters = null;
			ParameterizedCommand pc; 

			if (paramMap != null) {
				// Set parameters of the command to launch :
				parameters = new ArrayList<Parameterization>();
				Parameterization parameterization;
				for (String id : paramMap.keySet()) {
					parameterization = new Parameterization(
							cmd.getParameter(id), paramMap.get(id));
					parameters.add(parameterization);
				}
				pc = new ParameterizedCommand(cmd,
						parameters.toArray(new Parameterization[parameters.size()]));
			} else 
				pc = new ParameterizedCommand(cmd, null);
			
			// build the parameterized command
			// execute the command
			handlerService.executeCommand(pc, null);
		} catch (Exception e) {
			throw new ArgeoException(
					"Unexepected exception while opening node editor", e);
		}
	}

}
