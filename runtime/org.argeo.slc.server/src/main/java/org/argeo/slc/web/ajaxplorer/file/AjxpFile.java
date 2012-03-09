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
package org.argeo.slc.web.ajaxplorer.file;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.web.ajaxplorer.AjxpDriverException;

public class AjxpFile {
	private final static Log log = LogFactory.getLog(AjxpFile.class);

	// FIXME: more generic modif time format?
	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm");

	private final File file;
	private final String parentPath;
	private final String filePath;

	private final String ext;
	private final FileType type;

	public AjxpFile(File file, String parentPath) {
		this.file = file;
		this.parentPath = parentPath;
		if (parentPath.equals("/")) {
			this.filePath = "/" + file.getName();
		} else {
			this.filePath = parentPath + "/" + file.getName();
		}
		this.ext = file.isDirectory() ? null : file.getName().substring(
				file.getName().indexOf('.') + 1);
		this.type = FileType.findType(ext);
	}

	public String toXml(final LsMode mode, final String encoding) {
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("<tree");
			addAttr("text", file.getName(), buf);
			if (type != FileType.FOLDER) {
				if (mode == LsMode.SEARCH)
					addAttr("is_file", "true", buf);// FIXME: consistent value?
				else if (mode == LsMode.FILE_LIST) {
					addAttr("filename", filePath, buf);
					addAttr("is_file", "1", buf);
					addAttr("icon", type.getIcon(), buf);

					addAttr("modiftime", formatModifTime(), buf);
					addAttr("mimestring", type.getMimeString(), buf);
					addAttr("filesize", formatFileSize(), buf);

					if (type.isImage()) {
						addAttr("is_image", "1", buf);
						addAttr("image_type", type.getImageType(), buf);
						addAttr("image_width", "100", buf);// FIXME: read image
						addAttr("image_height", "100", buf);// FIXME: read image

					} else {
						addAttr("is_image", "0", buf);
					}
				}

			} else {// dir
				if (mode == LsMode.NULL || mode == LsMode.FILE_LIST) {
					addAttr("filename", filePath, buf);
					if (mode == LsMode.NULL) {
						addAttr("icon", "client/images/foldericon.png", buf);
						addAttr("openicon", "client/images/openfoldericon.png",
								buf);
						addAttr("parentName", parentPath, buf);
						addAttr("src", "content.php?dir="
								+ URLEncoder.encode(filePath, encoding), buf);
						addAttr(
								"action",
								"javascript:ajaxplorer.getFoldersTree().clickNode(CURRENT_ID)",
								buf);
					} else if (mode == LsMode.FILE_LIST) {
						addAttr("icon", type.getIcon(), buf);// FIXME:
						// consistent?
						addAttr("is_file", "0", buf);
						addAttr("is_image", "0", buf);
						addAttr("mimestring", "Directory", buf);
						addAttr("modiftime", formatModifTime(), buf);
						addAttr("filesize", "-", buf);
					}
				}

			}

			addAdditionalAttrs(buf, mode, encoding);

			buf.append("/>");

			if (log.isTraceEnabled())
				log.trace(buf.toString());

			return buf.toString();
		} catch (Exception e) {
			throw new AjxpDriverException("Could not serialize file " + file, e);
		}
	}

	private String formatModifTime() {
		return sdf.format(new Date(file.lastModified()));
	}

	private String formatFileSize() {
		return (file.length() / 1024) + " Kb";
	}

	protected void addAttr(String attrName, String attrValue, StringBuffer buf) {
		buf.append(" ").append(attrName).append("=\"").append(attrValue)
				.append("\"");
	}

	/** To be overridden, do nothing by default. */
	protected void addAdditionalAttrs(final StringBuffer buf,
			final LsMode mode, final String encoding) {

	}
}
