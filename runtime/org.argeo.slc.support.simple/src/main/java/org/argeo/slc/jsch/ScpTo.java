package org.argeo.slc.jsch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public class ScpTo extends AbstractJschTask {
	private final static Log log = LogFactory.getLog(ScpTo.class);

	private Resource localResource;
	private String remotePath;

	private String dir;
	private String remoteDir;
	private List<String> includes = new ArrayList<String>();

	private List<String> excludes = new ArrayList<String>();

	private PathMatcher pathMatcher;

	public void run(Session session) {
		if (StringUtils.hasText(dir)) {
			if (!StringUtils.hasText(remoteDir))
				throw new SlcException("Remote dir has to be specified.");

			String dirOs = dir.replace('/', File.separatorChar);
			if (dirOs.charAt(dir.length() - 1) != File.separatorChar) {
				dirOs = dirOs + File.separator;
			}

			if (pathMatcher == null)
				pathMatcher = new AntPathMatcher();

			log.info("Start multiple scp based on "+dirOs);
			scanDir(session, dirOs, "", includes, excludes);
		}

		if (localResource != null) {
			File lFile;
			try {
				lFile = localResource.getFile();
			} catch (IOException e) {
				throw new SlcException("Cannot interpret resource "
						+ localResource + " as file.", e);
			}
			uploadFile(session, lFile, remotePath);
		}
	}

	protected void scanDir(Session session, String dir, String currentRelPath,
			List<String> includes, List<String> excludes) {
		File[] files = new File(dir).listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				String relPath = currentRelPath.concat(file.getName());
				if (match(relPath, includes, excludes, false)) {
					uploadFile(session, file, remoteDir + '/' + relPath);
				}
			} else {
				String relPath = currentRelPath.concat(file.getName()).concat(
						"/");
				if (match(relPath, includes, excludes, true)) {
					String nextDir = dir.concat(file.getName()).concat(
							File.separator);
					scanDir(session, nextDir, relPath, includes, excludes);
				}
			}
		}
	}

	protected Boolean match(String path, List<String> includes,
			List<String> excludes, boolean matchStart) {
		for (String patternIn : includes) {
			boolean matchIn = matchStart ? pathMatcher.matchStart(patternIn,
					path) : pathMatcher.match(patternIn, path);
			if (matchIn) {
				// Could be included, check excludes
				boolean excluded = false;
				ex: for (String patternEx : excludes) {
					boolean matchEx = matchStart ? pathMatcher.matchStart(
							patternEx, path) : pathMatcher.match(patternEx,
							path);

					if (matchEx) {
						excluded = true;
						break ex;
					}
				}
				if (!excluded)
					return true;
			}
		}
		return false;
	}

	protected void uploadFile(Session session, File localFile, String remoteFile) {
		InputStream in = null;
		OutputStream channelOut;
		InputStream channelIn;
		try {

			// exec 'scp -t rfile' remotely
			String command = "scp -p -t " + remoteFile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			channelOut = channel.getOutputStream();
			channelIn = channel.getInputStream();

			channel.connect();
			checkAck(channelIn);

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = localFile.length();
			command = "C0644 " + filesize + " ";
			int index = localFile.getPath().lastIndexOf('/');
			if (index > 0) {
				command += localFile.getPath().substring(index + 1);
			} else {
				command += localFile.getPath();
			}
			command += "\n";

			channelOut.write(command.getBytes());
			channelOut.flush();
			checkAck(channelIn);

			if (log.isDebugEnabled())
				log.debug("Start copy of " + localFile + " to " + remoteFile
						+ " on " + getSshTarget() + "...");

			final long oneMB = 1024l;// in KB
			final long tenMB = 10 * oneMB;// in KB

			// send a content of lfile
			in = new FileInputStream(localFile);
			byte[] buf = new byte[1024];
			long cycleCount = 0;
			while (true) {
				int len = in.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				channelOut.write(buf, 0, len); // out.flush();
				if ((cycleCount % oneMB) == 0)// each 1 MB
					System.out.print('#');
				if ((cycleCount % (tenMB)) == 0)// each 10 MB
					System.out.print(" - " + cycleCount / tenMB + "0 MB\n");
				cycleCount++;
			}
			// send '\0'
			buf[0] = 0;
			channelOut.write(buf, 0, 1);
			channelOut.flush();
			checkAck(channelIn);

			log.info("\n" + (cycleCount) + " KB sent to server. ("
					+ (cycleCount / oneMB + " MB)"));

			if (log.isDebugEnabled())
				log.debug("Finished copy of " + localFile + " to " + remoteFile
						+ " on " + getSshTarget() + "...");

			IOUtils.closeQuietly(channelOut);

			channel.disconnect();
		} catch (Exception e) {
			throw new SlcException("Cannot copy " + localFile + " to "
					+ remoteFile, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void setLocalResource(Resource localFile) {
		this.localResource = localFile;
	}

	public void setRemotePath(String remoteFile) {
		this.remotePath = remoteFile;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

}