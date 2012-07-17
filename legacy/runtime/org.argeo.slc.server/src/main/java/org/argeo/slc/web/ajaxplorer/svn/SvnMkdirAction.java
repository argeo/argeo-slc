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
package org.argeo.slc.web.ajaxplorer.svn;

import java.io.File;

import org.argeo.slc.web.ajaxplorer.AjxpDriverException;
import org.argeo.slc.web.ajaxplorer.file.FileMkdirAction;
import org.tmatesoft.svn.core.SVNException;

public class SvnMkdirAction extends FileMkdirAction<SvnDriver> {
	@Override
	protected void postProcess(SvnDriver driver, File newDir) {
		try {
			driver.beginWriteAction(newDir.getParentFile());

			log.debug("SVN Add: " + newDir);
			driver.getManager().getWCClient().doAdd(newDir, true,
					newDir.isDirectory(), true, true);

			driver.commitAll("Commit new dir " + newDir.getName());
			driver.completeWriteAction(newDir.getParentFile());
		} catch (SVNException e) {
			throw new AjxpDriverException("Cannot commit new dir" + newDir, e);
		} finally {
			driver.rollbackWriteAction(newDir.getParentFile());
		}
	}

}
