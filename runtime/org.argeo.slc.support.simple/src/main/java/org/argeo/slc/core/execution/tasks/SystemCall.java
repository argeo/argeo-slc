package org.argeo.slc.core.execution.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.exec.launcher.CommandLauncher;
import org.apache.commons.exec.launcher.CommandLauncherFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;

public class SystemCall implements Runnable {
	// TODO: specify environment variables

	private final Log log = LogFactory.getLog(getClass());

	private String execDir;

	private String cmd = null;
	private List<String> command = null;

	private Boolean synchronous = true;
	// private Boolean captureStdIn = false;

	private String stdErrLogLevel = "ERROR";
	private String stdOutLogLevel = "INFO";

	private Map<String, List<String>> osCommands = new HashMap<String, List<String>>();
	private Map<String, String> osCmds = new HashMap<String, String>();
	private Map<String, String> environmentVariables = new HashMap<String, String>();

	private Long watchdogTimeout = 24 * 60 * 60 * 1000l;

	public void run() {
		try {
			if (log.isTraceEnabled()) {
				log.debug("os.name=" + System.getProperty("os.name"));
				log.debug("os.arch=" + System.getProperty("os.arch"));
				log.debug("os.version=" + System.getProperty("os.version"));
			}

			// Execution directory
			File dir = null;
			if (execDir != null) {
				// Replace '/' by local file separator, for portability
				execDir.replace('/', File.separatorChar);
				dir = new File(execDir).getCanonicalFile();
			}

			// Process process = null;

			// Check if an OS specific command overrides
			String osName = System.getProperty("os.name");
			List<String> commandToUse = null;
			if (osCommands.containsKey(osName))
				commandToUse = osCommands.get(osName);
			else
				commandToUse = command;
			String cmdToUse = null;
			if (osCmds.containsKey(osName))
				cmdToUse = osCmds.get(osName);
			else
				cmdToUse = cmd;

			// Prepare executor
			if (dir == null)
				dir = new File(getUsedDir(dir));
			if (!dir.exists())
				dir.mkdirs();

			Executor executor = new DefaultExecutor();
			executor.setWatchdog(new ExecuteWatchdog(watchdogTimeout));
			PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(
					new LogOutputStream() {
						protected void processLine(String line, int level) {
							log(stdOutLogLevel, line);
						}
					}, new LogOutputStream() {
						protected void processLine(String line, int level) {
							log(stdErrLogLevel, line);
						}
					}, null);
			executor.setStreamHandler(pumpStreamHandler);
			executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
			executor.setWorkingDirectory(dir);
			final CommandLine commandLine;

			// Which command definition to use
			if (commandToUse == null && cmdToUse == null)
				throw new SlcException("Please specify a command.");
			else if (commandToUse != null && cmdToUse != null)
				throw new SlcException(
						"Specify the command either as a line or as a list.");
			else if (cmdToUse != null) {
				if (log.isTraceEnabled())
					log.trace("Execute '" + cmdToUse + "' in "
							+ getUsedDir(dir));

				commandLine = CommandLine.parse(cmdToUse);
				// process = Runtime.getRuntime().exec(cmdToUse, null, dir);
			} else if (commandToUse != null) {
				if (log.isTraceEnabled())
					log.trace("Execute '" + commandToUse + "' in "
							+ getUsedDir(dir));
				if (commandToUse.size() == 0)
					throw new SlcException("Command line is empty.");

				commandLine = new CommandLine(commandToUse.get(0));
				for (int i = 1; i < commandToUse.size(); i++)
					commandLine.addArgument(commandToUse.get(i));
				// ProcessBuilder processBuilder = new
				// ProcessBuilder(commandToUse);
				// processBuilder.directory(dir);
				// process = processBuilder.start();
			} else {
				// all cases covered previously
				throw new NotImplementedException();
			}

			// Env variables
			Map<String, String> environmentVariablesToUse = environmentVariables
					.size() > 0 ? environmentVariables : null;

			// Execute
			ExecuteResultHandler executeResultHandler = new ExecuteResultHandler() {

				public void onProcessComplete(int exitValue) {
					log.info("Process " + commandLine + " properly completed.");
				}

				public void onProcessFailed(ExecuteException e) {
					throw new SlcException("Process " + commandLine
							+ " failed.", e);
				}
			};

			if (synchronous)
				try {
					int exitValue = executor.execute(commandLine,
							environmentVariablesToUse);
					executeResultHandler.onProcessComplete(exitValue);
				} catch (ExecuteException e1) {
					executeResultHandler.onProcessFailed(e1);
				}
			else
				executor.execute(commandLine, environmentVariablesToUse,
						executeResultHandler);

			// Manage standard streams
			// StreamReaderThread stdOutThread = new StreamReaderThread(process
			// .getInputStream()) {
			// protected void callback(String line) {
			// stdOutCallback(line);
			// }
			// };
			// stdOutThread.start();
			// StreamReaderThread stdErrThread = new StreamReaderThread(process
			// .getErrorStream()) {
			// protected void callback(String line) {
			// stdErrCallback(line);
			// }
			// };
			// stdErrThread.start();
			// if (captureStdIn)
			// new StdInThread(process.getOutputStream()).start();
			//
			// // Wait for the end of the process
			// if (synchronous) {
			// Integer exitCode = process.waitFor();
			// if (exitCode != 0) {
			// Thread.sleep(5000);// leave the process a chance to log
			// log.warn("Process return exit code " + exitCode);
			// }
			// } else {
			// // asynchronous: return
			// }
		} catch (Exception e) {
			throw new SlcException("Could not execute command " + cmd, e);
		}

	}

	/**
	 * Shortcut method returning the current exec dir if the specified one is
	 * null.
	 */
	private String getUsedDir(File dir) {
		if (dir == null)
			return System.getProperty("user.dir");
		else
			return dir.getPath();
	}

	// protected void stdOutCallback(String line) {
	// log(stdOutLogLevel, line);
	// }
	//
	// protected void stdErrCallback(String line) {
	// log(stdErrLogLevel, line);
	// }
	//
	protected void log(String logLevel, String line) {
		if ("ERROR".equals(logLevel))
			log.error(line);
		else if ("WARN".equals(logLevel))
			log.warn(line);
		else if ("INFO".equals(logLevel))
			log.info(line);
		else if ("DEBUG".equals(logLevel))
			log.debug(line);
		else if ("TRACE".equals(logLevel))
			log.trace(line);
		else
			throw new SlcException("Unknown log level " + logLevel);
	}

	public void setCmd(String command) {
		this.cmd = command;
	}

	public void setExecDir(String execdir) {
		this.execDir = execdir;
	}

	public void setStdErrLogLevel(String stdErrLogLevel) {
		this.stdErrLogLevel = stdErrLogLevel;
	}

	public void setStdOutLogLevel(String stdOutLogLevel) {
		this.stdOutLogLevel = stdOutLogLevel;
	}

	public void setSynchronous(Boolean synchronous) {
		this.synchronous = synchronous;
	}

	// public void setCaptureStdIn(Boolean captureStdIn) {
	// this.captureStdIn = captureStdIn;
	// }

	public void setCommand(List<String> command) {
		this.command = command;
	}

	public void setOsCommands(Map<String, List<String>> osCommands) {
		this.osCommands = osCommands;
	}

	public void setOsCmds(Map<String, String> osCmds) {
		this.osCmds = osCmds;
	}

	// protected abstract class StreamReaderThread extends Thread {
	// private final InputStream stream;
	//
	// public StreamReaderThread(InputStream stream) {
	// this.stream = stream;
	// }
	//
	// @Override
	// public void run() {
	// BufferedReader in = null;
	// try {
	// in = new BufferedReader(new InputStreamReader(stream));
	// String line = null;
	// while ((line = in.readLine()) != null) {
	// stdOutCallback(line);
	// }
	// } catch (IOException e) {
	// if (log.isTraceEnabled()) {
	// log.trace("Could not read stream", e);
	// // catch silently
	// // because the other methods
	// // to check whether the stream
	// // is closed would probably
	// // be to costly
	// }
	// } finally {
	// if (synchronous)
	// IOUtils.closeQuietly(in);
	// }
	// }
	//
	// protected abstract void callback(String line);
	// }
	//
	// protected class StdInThread extends Thread {
	// private final OutputStream stream;
	//
	// public StdInThread(OutputStream stream) {
	// this.stream = stream;
	// }
	//
	// @Override
	// public void run() {
	// BufferedReader in = null;
	// Writer out = null;
	// try {
	// out = new OutputStreamWriter(stream);
	// in = new BufferedReader(new InputStreamReader(System.in));
	// String line = null;
	// while ((line = in.readLine()) != null) {
	// out.write(line);
	// out.write("\n");
	// out.flush();
	// }
	// } catch (IOException e) {
	// throw new SlcException("Could not write to stdin stream", e);
	// } finally {
	// if (synchronous) {
	// IOUtils.closeQuietly(in);
	// IOUtils.closeQuietly(out);
	// }
	// }
	// }
	//
	// }
}
