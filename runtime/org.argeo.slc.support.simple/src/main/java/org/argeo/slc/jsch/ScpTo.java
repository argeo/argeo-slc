package org.argeo.slc.jsch;

import java.io.File;
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

public class ScpTo extends AbstractJschTask {
	private final static Log log = LogFactory.getLog(ScpTo.class);

	private Resource localFile;
	private String remoteFile;

	public void run() {
		InputStream in = null;
		OutputStream channelOut;
		InputStream channelIn;

		Session session = openSession();
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
			File lFile = localFile.getFile();
			long filesize = lFile.length();
			command = "C0644 " + filesize + " ";
			int index = lFile.getPath().lastIndexOf('/');
			if (index > 0) {
				command += lFile.getPath().substring(index + 1);
			} else {
				command += lFile.getPath();
			}
			command += "\n";

			channelOut.write(command.getBytes());
			channelOut.flush();
			checkAck(channelIn);

			if (log.isDebugEnabled())
				log.debug("Start copy of " + localFile + " to " + remoteFile
						+ " on " + getSshTarget() + "...");

			// send a content of lfile
			in = localFile.getInputStream();
			byte[] buf = new byte[1024];
			long cycleCount = 0;
			while (true) {
				int len = in.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				channelOut.write(buf, 0, len); // out.flush();
				if ((cycleCount % 1024) == 0)// each 1 MB
					System.out.print('#');
				cycleCount++;
			}
			// send '\0'
			buf[0] = 0;
			channelOut.write(buf, 0, 1);
			channelOut.flush();
			checkAck(channelIn);

			if (log.isDebugEnabled())
				log.debug("Finished copy of " + localFile + " to " + remoteFile
						+ " on " + getSshTarget() + "...");

			IOUtils.closeQuietly(channelOut);

			channel.disconnect();
			session.disconnect();

		} catch (Exception e) {
			throw new SlcException("Cannot copy " + localFile + " to "
					+ remoteFile, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void setLocalFile(Resource localFile) {
		this.localFile = localFile;
	}

	public void setRemoteFile(String remoteFile) {
		this.remoteFile = remoteFile;
	}

}
