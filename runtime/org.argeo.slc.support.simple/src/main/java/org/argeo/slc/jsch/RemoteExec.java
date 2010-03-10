package org.argeo.slc.jsch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.ExecutionResources;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;

public class RemoteExec extends AbstractJschTask {
	private final static Log log = LogFactory.getLog(RemoteExec.class);

	private Boolean failOnBadExitStatus = true;

	private List<String> commands = new ArrayList<String>();
	private String command;
	private SystemCall systemCall;
	private List<SystemCall> systemCalls = new ArrayList<SystemCall>();
	private Resource script;
	private Boolean xForwarding = false;
	private Boolean agentForwarding = false;
	private Boolean forceShell = false;
	private Map<String, String> env = new HashMap<String, String>();
	private Resource stdIn = null;
	private Resource stdOut = null;
	private ExecutionResources executionResources;

	/**
	 * If set, stdout is written to it as a list of lines. Cleared before each
	 * run.
	 */
	private List<String> stdOutLines = null;
	private Boolean logEvenIfStdOutLines = false;
	private Boolean quiet = false;

	public void run(Session session) {
		List<String> commandsToUse = new ArrayList<String>(commands);
		String commandToUse = command;
		// convert system calls
		if (systemCall != null) {
			if (command != null)
				throw new SlcException("Cannot specify command AND systemCall");
			commandToUse = convertSystemCall(systemCall);
		}

		if (systemCalls.size() != 0) {
			if (commandsToUse.size() != 0)
				throw new SlcException(
						"Cannot specify commands AND systemCalls");
			for (SystemCall systemCall : systemCalls)
				commandsToUse.add(convertSystemCall(systemCall));
		}

		if (script != null) {
			// TODO: simply pass the script as a string command
			if (commandsToUse.size() != 0)
				throw new SlcException("Cannot specify commands and script");
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(script
						.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (!StringUtils.hasText(line))
						continue;
					commandsToUse.add(line);
				}
			} catch (IOException e) {
				throw new SlcException("Cannot read script " + script, e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}

		if (forceShell) {
			if (commandToUse.indexOf(';') >= 0
					|| commandToUse.indexOf('\n') >= 0) {
				StringTokenizer st = new StringTokenizer(commandToUse, ";\n");
				while (st.hasMoreTokens()) {
					String cmd = st.nextToken();
					commandsToUse.add(cmd);
				}
			} else {
				commandsToUse.add(commandToUse);
			}
			commandToUse = null;
		}

		// execute command(s)
		if (commandToUse != null) {
			if (commandsToUse.size() != 0)
				throw new SlcException(
						"Specify either a single command or a list of commands.");
			remoteExec(session, commandToUse);
		} else {
			if (commandsToUse.size() == 0)
				throw new SlcException(
						"Neither a single command or a list of commands has been specified.");

			remoteExec(session, commandsToUse, script != null ? "script "
					+ script.getFilename() : commandsToUse.size() + " commands");
		}
	}

	protected String convertSystemCall(SystemCall systemCall) {
		// TODO: prepend environment variables
		// TODO: deal with exec dir
		return systemCall.asCommand();
	}

	protected void remoteExec(Session session, final List<String> commands,
			String description) {
		try {
			final ChannelShell channel = (ChannelShell) session
					.openChannel("shell");
			channel.setInputStream(null);
			channel.setXForwarding(xForwarding);
			channel.setAgentForwarding(agentForwarding);
			channel.setEnv(new Hashtable<String, String>(env));

			/*
			 * // Choose the pty-type "vt102".
			 * ((ChannelShell)channel).setPtyType("vt102");
			 */
			// Writer thread
			final BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(channel.getOutputStream()));

			if (log.isDebugEnabled())
				log.debug("Run " + description + " on " + getSshTarget()
						+ "...");
			channel.connect();

			// write commands to shell
			Thread writerThread = new Thread("Shell writer " + getSshTarget()) {
				@Override
				public void run() {
					try {
						for (String line : commands) {
							if (!StringUtils.hasText(line))
								continue;
							writer.write(line);
							writer.newLine();
						}
						writer.append("exit");
						writer.newLine();
						writer.flush();
						// channel.disconnect();
					} catch (IOException e) {
						throw new SlcException("Cannot write to shell on "
								+ getSshTarget(), e);
					} finally {
						IOUtils.closeQuietly(writer);
					}
				}
			};
			writerThread.start();

			readStdOut(channel);
			checkExitStatus(channel);
			channel.disconnect();

		} catch (Exception e) {
			throw new SlcException("Cannot use SSH shell on " + getSshTarget(),
					e);
		}

	}

	protected void remoteExec(Session session, String command) {
		try {
			final ChannelExec channel = (ChannelExec) session
					.openChannel("exec");
			channel.setCommand(command);

			channel.setInputStream(null);
			channel.setXForwarding(xForwarding);
			channel.setAgentForwarding(agentForwarding);
			channel.setEnv(new Hashtable<String, String>(env));
			channel.setErrStream(null);

			// Standard Error
			readStdErr(channel);

			if (log.isDebugEnabled())
				log.debug("Run '" + command + "' on " + getSshTarget() + "...");
			channel.connect();

			if (stdIn != null) {
				Thread stdInThread = new Thread("Stdin " + getSshTarget()) {
					@Override
					public void run() {
						OutputStream out = null;
						try {
							out = channel.getOutputStream();
							IOUtils.copy(stdIn.getInputStream(), out);
						} catch (IOException e) {
							throw new SlcException("Cannot write stdin on "
									+ getSshTarget(), e);
						} finally {
							IOUtils.closeQuietly(out);
						}
					}
				};
				stdInThread.start();
			}
			readStdOut(channel);
			checkExitStatus(channel);
			channel.disconnect();
		} catch (Exception e) {
			throw new SlcException("Cannot execute remotely '" + command
					+ "' on " + getSshTarget(), e);
		}
	}

	protected void readStdErr(final ChannelExec channel) {
		new Thread("stderr " + getSshTarget()) {
			public void run() {
				BufferedReader stdErr = null;
				try {
					InputStream in = channel.getErrStream();
					stdErr = new BufferedReader(new InputStreamReader(in));
					String line = null;
					while ((line = stdErr.readLine()) != null) {
						if (!line.trim().equals(""))
							log.error(line);
					}
				} catch (IOException e) {
					if (log.isDebugEnabled())
						log.error("Cannot read stderr from " + getSshTarget(),
								e);
				} finally {
					IOUtils.closeQuietly(stdErr);
				}
			}
		}.start();
	}

	protected void readStdOut(Channel channel) {
		if (stdOut != null) {
			OutputStream localStdOut = createOutputStream(stdOut);
			try {
				IOUtils.copy(channel.getInputStream(), localStdOut);
			} catch (IOException e) {
				throw new SlcException("Cannot redirect stdout", e);
			} finally {
				IOUtils.closeQuietly(localStdOut);
			}
		} else {
			BufferedReader stdOut = null;
			try {
				InputStream in = channel.getInputStream();
				stdOut = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = stdOut.readLine()) != null) {
					if (!line.trim().equals("")) {

						if (stdOutLines != null) {
							stdOutLines.add(line);
							if (logEvenIfStdOutLines && !quiet)
								log.info(line);
						} else {
							if (!quiet)
								log.info(line);
						}
					}
				}
			} catch (IOException e) {
				if (log.isDebugEnabled())
					log.error("Cannot read stdout from " + getSshTarget(), e);
			} finally {
				IOUtils.closeQuietly(stdOut);
			}
		}
	}

	protected void checkExitStatus(Channel channel) {
		if (channel.isClosed()) {
			int exitStatus = channel.getExitStatus();
			if (exitStatus == 0) {
				if (log.isTraceEnabled())
					log.trace("Remote execution exit status: " + exitStatus);
			} else {
				String msg = "Remote execution failed with " + " exit status: "
						+ exitStatus;
				if (failOnBadExitStatus)
					throw new SlcException(msg);
				else
					log.error(msg);
			}
		}

	}

	protected OutputStream createOutputStream(Resource target) {
		FileOutputStream out = null;
		try {

			final File file;
			if (executionResources != null)
				file = new File(executionResources.getAsOsPath(target, true));
			else
				file = target.getFile();
			out = new FileOutputStream(file, false);
		} catch (IOException e) {
			log.error("Cannot get file for " + target, e);
			IOUtils.closeQuietly(out);
		}
		return out;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	public void setFailOnBadExitStatus(Boolean failOnBadExitStatus) {
		this.failOnBadExitStatus = failOnBadExitStatus;
	}

	public void setSystemCall(SystemCall systemCall) {
		this.systemCall = systemCall;
	}

	public void setSystemCalls(List<SystemCall> systemCalls) {
		this.systemCalls = systemCalls;
	}

	public void setScript(Resource script) {
		this.script = script;
	}

	public void setxForwarding(Boolean xForwarding) {
		this.xForwarding = xForwarding;
	}

	public void setAgentForwarding(Boolean agentForwarding) {
		this.agentForwarding = agentForwarding;
	}

	public void setEnv(Map<String, String> env) {
		this.env = env;
	}

	public void setForceShell(Boolean forceShell) {
		this.forceShell = forceShell;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void setStdOutLines(List<String> stdOutLines) {
		this.stdOutLines = stdOutLines;
	}

	public void setLogEvenIfStdOutLines(Boolean logEvenIfStdOutLines) {
		this.logEvenIfStdOutLines = logEvenIfStdOutLines;
	}

	public void setQuiet(Boolean quiet) {
		this.quiet = quiet;
	}

	public void setStdIn(Resource stdIn) {
		this.stdIn = stdIn;
	}

	public void setStdOut(Resource stdOut) {
		this.stdOut = stdOut;
	}

	public void setExecutionResources(ExecutionResources executionResources) {
		this.executionResources = executionResources;
	}

}
