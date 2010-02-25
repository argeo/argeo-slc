package org.argeo.slc.jsch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

			log.info("Start multiple scp based on " + dirOs);
			scanDir(session, dirOs, "", includes, excludes);
		}

		if (localResource != null) {
			uploadResource(session, localResource, remoteDir);
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

	protected void uploadFile(Session session, File file, String remoteFile) {
		try {
			uploadFile(session, new FileInputStream(file), file.length(), file
					.getPath(), file.toString(), remoteFile);
		} catch (FileNotFoundException e) {
			throw new SlcException("Cannot upload " + file, e);
		}
	}

	protected void uploadResource(Session session, Resource resource,
			String remoteFile) {
		try {
			File lFile = resource.getFile();
			uploadFile(session, lFile, remotePath);
		} catch (IOException e) {
			// no underlying file found
			// load the resource in memory before transferring it
			InputStream in = null;
			try {
				in = resource.getInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				IOUtils.copy(in, out);
				byte[] arr = out.toByteArray();
				ByteArrayInputStream content = new ByteArrayInputStream(arr);
				uploadFile(session, content, arr.length, resource.getURL()
						.getPath(), resource.toString(), remotePath);
				arr = null;
			} catch (IOException e1) {
				throw new SlcException("Can neither interpret resource "
						+ localResource
						+ " as file, nor create a temporary file", e1);
			} finally {
				IOUtils.closeQuietly(in);
				// no need to close byte arrays streams
			}
		}
	}

	protected void uploadFile(Session session, InputStream in, long size,
			String path, String sourceDesc, String remoteFile) {
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
			long filesize = size;
			command = "C0644 " + filesize + " ";
			int index = path.lastIndexOf('/');
			if (index > 0) {
				command += path.substring(index + 1);
			} else {
				command += path;
			}
			command += "\n";

			channelOut.write(command.getBytes());
			channelOut.flush();
			checkAck(channelIn);

			if (log.isTraceEnabled())
				log.debug("Start copy of " + sourceDesc + " to " + remoteFile
						+ " on " + getSshTarget() + "...");

			final long oneMB = 1024l;// in KB
			final long tenMB = 10 * oneMB;// in KB

			// send a content of lfile
			byte[] buf = new byte[1024];
			long cycleCount = 0;
			long nbrOfBytes = 0;
			while (true) {
				int len = in.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				channelOut.write(buf, 0, len); // out.flush();
				nbrOfBytes = nbrOfBytes + len;
				if (((cycleCount % oneMB) == 0) && cycleCount != 0)// each 1 MB
					System.out.print('#');
				if (((cycleCount % (tenMB)) == 0) && cycleCount != 0)// each 10
					// MB
					System.out.print(" - " + cycleCount / tenMB + "0 MB\n");
				cycleCount++;
			}
			// send '\0'
			buf[0] = 0;
			channelOut.write(buf, 0, 1);
			channelOut.flush();
			checkAck(channelIn);

			if (log.isDebugEnabled())
				log.debug("Transferred to " + remoteFile + " ("
						+ sizeDesc(nbrOfBytes) + ") on " + getSshTarget()
						+ " from " + sourceDesc);

			IOUtils.closeQuietly(channelOut);

			channel.disconnect();
		} catch (Exception e) {
			throw new SlcException("Cannot copy " + path + " to " + remoteFile,
					e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	protected String sizeDesc(Long nbrOfBytes) {
		if (nbrOfBytes < 1024)
			return nbrOfBytes + " B";
		else if (nbrOfBytes < 1024 * 1024)
			return (nbrOfBytes / 1024) + " KB";
		else
			return nbrOfBytes / (1024 * 1024) + " MB";
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
