package org.argeo.slc.web.ajaxplorer.file;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.argeo.slc.web.ajaxplorer.AjxpAnswer;

public class FileMkdirAction<T extends FileDriver> extends FileAction {

	public AjxpAnswer execute(FileDriver driver,
			HttpServletRequest request) {
		String dir = request.getParameter("dir");
		String dirName = request.getParameter("dirname");

		File newDir = driver.getFile(dir, dirName);
		newDir.mkdirs();

		postProcess((T)driver,newDir);

		return AjxpAnswer.DO_NOTHING;
	}

	protected void postProcess(T driver,File newDir) {

	}
}
