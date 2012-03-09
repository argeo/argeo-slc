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

import org.argeo.slc.web.ajaxplorer.file.AjxpFile;
import org.argeo.slc.web.ajaxplorer.file.LsMode;
import org.tmatesoft.svn.core.wc.SVNInfo;

public class SvnAjxpFile extends AjxpFile {

	protected final SVNInfo info;

	public SvnAjxpFile(SVNInfo info, String parentPath) {
		super(info.getFile(), parentPath);
		this.info = info;
	}

	@Override
	protected void addAdditionalAttrs(StringBuffer buf, LsMode mode,
			String encoding) {
		addAttr("author", info.getAuthor(), buf);
		addAttr("revision", Long.toString(info.getRevision().getNumber()), buf);
	}

}
