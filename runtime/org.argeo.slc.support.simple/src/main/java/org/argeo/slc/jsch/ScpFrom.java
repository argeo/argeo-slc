package org.argeo.slc.jsch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public class ScpFrom extends AbstractJschTask {
	private final static Log log = LogFactory.getLog(ScpFrom.class);

	private Resource localResource;
	private String remotePath;
	private Boolean mkdir = false;

	public void run(Session session) {
		if (localResource != null) {
			File lFile;
			try {
				lFile = localResource.getFile();
			} catch (IOException e) {
				throw new SlcException("Cannot interpret resource "
						+ localResource + " as file.", e);
			}
			downloadFile(session, lFile, remotePath);
		}
	}

	protected void downloadFile(Session session, File localFile,
			String remoteFile) {
		OutputStream out = null;
		OutputStream channelOut;
		InputStream channelIn;
		try {
			// exec 'scp -f rfile' remotely
			String command = "scp -f " + remoteFile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			channelOut = channel.getOutputStream();
			channelIn = channel.getInputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			channelOut.write(buf, 0, 1);
			channelOut.flush();

			while (true) {
				int c = checkAck(channelIn);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				channelIn.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (channelIn.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String remoteFileName = null;
				for (int i = 0;; i++) {
					channelIn.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						remoteFileName = new String(buf, 0, i);
						break;
					}
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				channelOut.write(buf, 0, 1);
				channelOut.flush();

				// Create a s adirectory if it doesn't exists
				if (!localFile.exists() && mkdir)
					localFile.mkdirs();

				// read a content of lfile
				String localPath = localFile.isDirectory() ? localFile
						.getPath()
						+ File.separator + remoteFileName : localFile.getPath();
				if (log.isDebugEnabled())
					log.debug("Download " + remoteFile + " to " + localPath);

				out = new FileOutputStream(localPath);
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = channelIn.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					out.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}

				checkAck(channelIn);

				// send '\0'
				buf[0] = 0;
				channelOut.write(buf, 0, 1);
				channelOut.flush();
			}

			channel.disconnect();
			// session.disconnect();
		} catch (Exception e) {
			throw new SlcException("Cannot download " + remoteFile + " to "
					+ localFile, e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public void setLocalResource(Resource localFile) {
		this.localResource = localFile;
	}

	public void setRemotePath(String remoteFile) {
		this.remotePath = remoteFile;
	}

	public void setMkdir(Boolean mkdir) {
		this.mkdir = mkdir;
	}
}
