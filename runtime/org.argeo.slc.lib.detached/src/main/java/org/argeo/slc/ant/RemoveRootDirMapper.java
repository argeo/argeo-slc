package org.argeo.slc.ant;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.FileNameMapper;

public class RemoveRootDirMapper implements FileNameMapper {
	private Log log = LogFactory.getLog(RemoveRootDirMapper.class);
	private String to = "enabled";

	public String[] mapFileName(String sourceFileName) {
		StringTokenizer st = new StringTokenizer(sourceFileName, "/");
		boolean first = true;
		boolean skipRoot = !to.equals("disabled");
		StringBuffer buf = new StringBuffer("");
		while (st.hasMoreTokens()) {
			if (first && skipRoot) { // skip
				st.nextToken();
				first = false;
			} else {
				buf.append(st.nextToken()).append('/');
			}
		}

		if (log.isTraceEnabled()) {
			log.trace("Source: " + sourceFileName + " - out: " + buf);
		}
		return new String[] { buf.toString() };
	}

	public void setFrom(String from) {
	}

	public void setTo(String to) {
		this.to = to;
	}

}
