package org.argeo.slc.core.execution.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRelatedHelper;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestStatus;
import org.springframework.core.io.Resource;

/** Execute and OS system call. */
public class SystemCall extends TreeSRelatedHelper implements Runnable,
		StructureAware<TreeSPath> {
	private final Log log = LogFactory.getLog(getClass());

	private String execDir;

	private String cmd = null;
	private List<Object> command = null;

	private Boolean synchronous = true;

	private String stdErrLogLevel = "ERROR";
	private String stdOutLogLevel = "INFO";

	private Resource stdOutFile = null;
	private Resource stdErrFile = null;

	private Map<String, List<Object>> osCommands = new HashMap<String, List<Object>>();
	private Map<String, String> osCmds = new HashMap<String, String>();
	private Map<String, String> environmentVariables = new HashMap<String, String>();

	private Boolean logCommand = false;
	private Boolean redirectStreams = true;
	private String osConsole = null;
	private String generateScript = null;

	private Long watchdogTimeout = 24 * 60 * 60 * 1000l;

	private TestResult testResult;

	// Internal use

	public SystemCall() {

	}

	public SystemCall(List<Object> command) {
		super();
		this.command = command;
	}

	public void run() {
		final Writer stdOutWriter;
		final Writer stdErrWriter;
		if (stdOutFile != null) {
			stdOutWriter = createWriter(stdOutFile);
		} else
			stdOutWriter = null;
		if (stdErrFile != null) {
			stdErrWriter = createWriter(stdErrFile);
		} else {
			if (stdOutFile != null) {
				stdErrWriter = createWriter(stdOutFile);
			} else
				stdErrWriter = null;
		}

		try {
			if (log.isTraceEnabled()) {
				log.debug("os.name=" + System.getProperty("os.name"));
				log.debug("os.arch=" + System.getProperty("os.arch"));
				log.debug("os.version=" + System.getProperty("os.version"));
			}

			// Execution directory
			File dir = new File(getExecDirToUse());
			if (!dir.exists())
				dir.mkdirs();

			// Watchdog to check for lost processes
			Executor executor = new DefaultExecutor();
			executor.setWatchdog(new ExecuteWatchdog(watchdogTimeout));

			if (redirectStreams) {
				// Redirect standard streams
				executor.setStreamHandler(createExecuteStreamHandler(
						stdOutWriter, stdErrWriter));
			} else {
				// Dummy stream handler (otherwise pump is used)
				executor.setStreamHandler(new DummyexecuteStreamHandler());
			}

			executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
			executor.setWorkingDirectory(dir);

			// Command line to use
			final CommandLine commandLine = createCommandLine();
			if (logCommand)
				log.info("Execute command:\n" + commandLine + "\n");

			// Env variables
			Map<String, String> environmentVariablesToUse = environmentVariables
					.size() > 0 ? environmentVariables : null;

			// Execute
			ExecuteResultHandler executeResultHandler = createExecuteResultHandler(commandLine);

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
		} catch (Exception e) {
			throw new SlcException("Could not execute command " + cmd, e);
		} finally {
			IOUtils.closeQuietly(stdOutWriter);
			IOUtils.closeQuietly(stdErrWriter);
		}

	}

	/** Can be overridden by specific command wrapper */
	protected CommandLine createCommandLine() {
		// Check if an OS specific command overrides
		String osName = System.getProperty("os.name");
		List<Object> commandToUse = null;
		if (osCommands.containsKey(osName))
			commandToUse = osCommands.get(osName);
		else
			commandToUse = command;
		String cmdToUse = null;
		if (osCmds.containsKey(osName))
			cmdToUse = osCmds.get(osName);
		else
			cmdToUse = cmd;

		CommandLine commandLine = null;

		// Which command definition to use
		if (commandToUse == null && cmdToUse == null)
			throw new SlcException("Please specify a command.");
		else if (commandToUse != null && cmdToUse != null)
			throw new SlcException(
					"Specify the command either as a line or as a list.");
		else if (cmdToUse != null) {
			commandLine = CommandLine.parse(cmdToUse);
		} else if (commandToUse != null) {
			if (commandToUse.size() == 0)
				throw new SlcException("Command line is empty.");

			commandLine = new CommandLine(commandToUse.get(0).toString());

			for (int i = 1; i < commandToUse.size(); i++) {
				if (log.isTraceEnabled())
					log.debug(commandToUse.get(i));
				commandLine.addArgument(commandToUse.get(i).toString());
			}
		} else {
			// all cases covered previously
			throw new UnsupportedException();
		}

		if (generateScript != null) {
			File scriptFile = new File(getExecDirToUse() + File.separator
					+ generateScript);
			try {
				FileUtils.writeStringToFile(scriptFile,
						(osConsole != null ? osConsole + " " : "")
								+ commandLine.toString());
			} catch (IOException e) {
				throw new SlcException("Could not generate script "
						+ scriptFile, e);
			}
			commandLine = new CommandLine(scriptFile);
		} else {
			if (osConsole != null)
				commandLine = CommandLine.parse(osConsole + " "
						+ commandLine.toString());
		}

		return commandLine;
	}

	protected ExecuteStreamHandler createExecuteStreamHandler(
			final Writer stdOutWriter, final Writer stdErrWriter) {
		// Log writers

		PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(
				new LogOutputStream() {
					protected void processLine(String line, int level) {
						log(stdOutLogLevel, line);
						if (stdOutWriter != null)
							appendLineToFile(stdOutWriter, line);
					}
				}, new LogOutputStream() {
					protected void processLine(String line, int level) {
						log(stdErrLogLevel, line);
						if (stdErrWriter != null)
							appendLineToFile(stdErrWriter, line);
					}
				}, null);
		return pumpStreamHandler;
	}

	protected ExecuteResultHandler createExecuteResultHandler(
			final CommandLine commandLine) {
		return new ExecuteResultHandler() {

			public void onProcessComplete(int exitValue) {
				if (log.isDebugEnabled())
					log
							.debug("Process " + commandLine
									+ " properly completed.");
				if (testResult != null) {
					forwardPath(testResult, null);
					testResult.addResultPart(new SimpleResultPart(
							TestStatus.PASSED, "Process " + commandLine
									+ " properly completed."));
				}
			}

			public void onProcessFailed(ExecuteException e) {
				if (testResult != null) {
					forwardPath(testResult, null);
					testResult.addResultPart(new SimpleResultPart(
							TestStatus.ERROR, "Process " + commandLine
									+ " failed.", e));
				} else {
					throw new SlcException("Process " + commandLine
							+ " failed.", e);
				}
			}
		};
	}

	/**
	 * Shortcut method getting the execDir to use
	 */
	protected String getExecDirToUse() {
		try {
			File dir = null;
			if (execDir != null) {
				// Replace '/' by local file separator, for portability
				execDir.replace('/', File.separatorChar);
				dir = new File(execDir).getCanonicalFile();
			}

			if (dir == null)
				return System.getProperty("user.dir");
			else
				return dir.getPath();
		} catch (Exception e) {
			throw new SlcException("Cannot find exec dir", e);
		}
	}

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

	protected void appendLineToFile(Writer writer, String line) {
		try {
			writer.append(line).append('\n');
		} catch (IOException e) {
			log.error("Cannot write to log file", e);
		}
	}

	protected Writer createWriter(Resource target) {
		FileWriter writer = null;
		try {
			File file = target.getFile();
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			log.error("Cannot create log file " + target, e);
			IOUtils.closeQuietly(writer);
		}
		return writer;
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

	public void setCommand(List<Object> command) {
		this.command = command;
	}

	public void setOsCommands(Map<String, List<Object>> osCommands) {
		this.osCommands = osCommands;
	}

	public void setOsCmds(Map<String, String> osCmds) {
		this.osCmds = osCmds;
	}

	public void setEnvironmentVariables(Map<String, String> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}

	public void setWatchdogTimeout(Long watchdogTimeout) {
		this.watchdogTimeout = watchdogTimeout;
	}

	public void setStdOutFile(Resource stdOutFile) {
		this.stdOutFile = stdOutFile;
	}

	public void setStdErrFile(Resource stdErrFile) {
		this.stdErrFile = stdErrFile;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public void setLogCommand(Boolean logCommand) {
		this.logCommand = logCommand;
	}

	public void setRedirectStreams(Boolean redirectStreams) {
		this.redirectStreams = redirectStreams;
	}

	public void setOsConsole(String osConsole) {
		this.osConsole = osConsole;
	}

	public void setGenerateScript(String generateScript) {
		this.generateScript = generateScript;
	}

	private class DummyexecuteStreamHandler implements ExecuteStreamHandler {

		public void setProcessErrorStream(InputStream is) throws IOException {
		}

		public void setProcessInputStream(OutputStream os) throws IOException {
		}

		public void setProcessOutputStream(InputStream is) throws IOException {
		}

		public void start() throws IOException {
		}

		public void stop() {
		}

	}

}
