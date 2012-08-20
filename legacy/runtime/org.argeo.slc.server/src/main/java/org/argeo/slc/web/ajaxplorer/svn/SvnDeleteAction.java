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
import org.argeo.slc.web.ajaxplorer.file.FileDeleteAction;
import org.tmatesoft.svn.core.SVNException;

public class SvnDeleteAction extends FileDeleteAction<SvnDriver> {
	@Override
	protected void executeDelete(SvnDriver driver, File file) {
		try {
			driver.beginWriteAction(file.getParentFile());

			log.debug("SVN Delete: " + file);
			driver.getManager().getWCClient().doDelete(file, true, false);

			driver.commitAll("Commit delete of " + file.getName());
			driver.completeWriteAction(file.getParentFile());
		} catch (SVNException e) {
			throw new AjxpDriverException("Cannot delete file " + file, e);
		} finally {
			driver.rollbackWriteAction(file.getParentFile());
		}
	}

}