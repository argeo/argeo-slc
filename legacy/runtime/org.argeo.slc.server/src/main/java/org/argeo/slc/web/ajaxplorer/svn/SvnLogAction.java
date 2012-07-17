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
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.web.ajaxplorer.AjxpAction;
import org.argeo.slc.web.ajaxplorer.AjxpAnswer;
import org.argeo.slc.web.ajaxplorer.AjxpDriverException;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SvnLogAction implements AjxpAction<SvnDriver> {
	private final SimpleDateFormat sdfIso = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private final Log log = LogFactory.getLog(getClass());

	public AjxpAnswer execute(SvnDriver driver, HttpServletRequest request) {
		String fileStr = request.getParameter("file");
		log.debug("Log file " + fileStr);
		if (fileStr == null) {
			throw new AjxpDriverException("A  file needs to be provided.");
		}
		File file = new File(driver.getBasePath() + fileStr);
		return new SvnLogAnswer(driver, file);
	}

	protected class SvnLogAnswer implements AjxpAnswer {
		private final SvnDriver driver;
		private final File file;

		public SvnLogAnswer(SvnDriver driver, File file) {
			this.driver = driver;
			this.file = file;
		}

		public void updateResponse(HttpServletResponse response) {
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.append("<tree>");
				writer.append("<log>");

				final List<SVNLogEntry> logEntries = new Vector<SVNLogEntry>();
				ISVNLogEntryHandler logHandler = new ISVNLogEntryHandler() {
					public void handleLogEntry(SVNLogEntry logEntry)
							throws SVNException {
						logEntries.add(logEntry);
					}
				};

				driver.getManager().getLogClient().doLog(new File[] { file },
						SVNRevision.create(0), SVNRevision.HEAD, true, true,
						100, logHandler);

				for (int i = logEntries.size() - 1; i >= 0; i--) {
					String xml = logEntryAsXml(logEntries.get(i), file);
					if(log.isTraceEnabled())
						log.trace(xml);
					writer.append(xml);
				}

				writer.append("</log>");
				writer.append("</tree>");
			} catch (Exception e) {
				throw new AjxpDriverException(
						"Cannot retrieve log for " + file, e);
			} finally {
				IOUtils.closeQuietly(writer);
			}
		}

	}

	protected String logEntryAsXml(SVNLogEntry entry, File file) {
		StringBuffer buf = new StringBuffer();
		buf.append("<logentry");
		buf.append(" revision=\"").append(entry.getRevision()).append("\"");
		buf.append(" is_file=\"").append(file.isDirectory() ? "0" : "1")
				.append("\"");
		buf.append(">");

		buf.append("<author>").append(entry.getAuthor()).append("</author>");
		buf.append("<date>").append(sdfIso.format(entry.getDate())).append(
				"</date>");

		buf.append("<paths>");
		Map<Object, SVNLogEntryPath> paths = entry.getChangedPaths();
		for (SVNLogEntryPath path : paths.values()) {
			buf.append("<path>").append(path.getPath()).append("</path>");
		}
		buf.append("</paths>");

		buf.append("<msg>").append(entry.getMessage()).append("</msg>");

		buf.append("</logentry>");
		return buf.toString();
	}

}
