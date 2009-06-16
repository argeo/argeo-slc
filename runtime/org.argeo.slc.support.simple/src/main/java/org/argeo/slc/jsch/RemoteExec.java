package org.argeo.slc.jsch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public class RemoteExec extends AbstractJschTask {
	private final static Log log = LogFactory.getLog(RemoteExec.class);

	private List<String> commands = new ArrayList<String>();
	private String command;

	public void run(Session session) {
		if (command != null) {
			if (commands.size() != 0)
				throw new SlcException(
						"Specify either a single command or a list of commands.");
			remoteExec(session, command);
		} else {
			if (commands.size() == 0)
				throw new SlcException(
						"Neither a single command or a list of commands has been specified.");

			for (String cmd : commands) {
				remoteExec(session, cmd);
			}
		}
	}

	protected void remoteExec(Session session, String command) {
		BufferedReader execIn = null;
		try {
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// X Forwarding
			// channel.setXForwarding(true);

			// channel.setInputStream(System.in);
			channel.setInputStream(null);

			// channel.setOutputStream(System.out);

			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			if (log.isDebugEnabled())
				log
						.debug("Exec '" + command + "' on " + getSshTarget()
								+ "...");

			channel.connect();

			// byte[] tmp = new byte[1024];
			while (true) {
				execIn = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = execIn.readLine()) != null) {
					log.info(line);
				}
				// while (in.available() > 0) {
				// int i = in.read(tmp, 0, 1024);
				// if (i < 0)
				// break;
				// log.info(new String(tmp, 0, i));
				// }
				if (channel.isClosed()) {
					log.info("Remote execution exit status: "
							+ channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
		} catch (Exception e) {
			throw new SlcException("Cannot execute remotely '" + command
					+ "' on " + getSshTarget(), e);
		} finally {
			IOUtils.closeQuietly(execIn);
		}
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

}
