package org.argeo.slc.runtime.tasks;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.TRACE;
import static java.lang.System.Logger.Level.WARNING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.security.auth.callback.CallbackHandler;

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
import org.argeo.api.slc.SlcException;
import org.argeo.api.slc.UnsupportedException;
import org.argeo.api.slc.execution.ExecutionResources;
import org.argeo.api.slc.test.TestResult;
import org.argeo.api.slc.test.TestStatus;
import org.argeo.slc.runtime.test.SimpleResultPart;

/** Execute an OS specific system call. */
public class SystemCall implements Runnable {
	public final static String LOG_STDOUT = "System.out";

	private final Logger logger = System.getLogger(getClass().getName());

	private String execDir;

	private String cmd = null;
	private List<Object> command = null;

	private Executor executor = new DefaultExecutor();
	private Boolean synchronous = true;

	private String stdErrLogLevel = "ERROR";
	private String stdOutLogLevel = "INFO";

	private Path stdOutFile = null;
	private Path stdErrFile = null;

	private Path stdInFile = null;
	/**
	 * If no {@link #stdInFile} provided, writing to this stream will write to the
	 * stdin of the process.
	 */
	private OutputStream stdInSink = null;

	private Boolean redirectStdOut = false;

	private List<SystemCallOutputListener> outputListeners = Collections
			.synchronizedList(new ArrayList<SystemCallOutputListener>());

	private Map<String, List<Object>> osCommands = new HashMap<String, List<Object>>();
	private Map<String, String> osCmds = new HashMap<String, String>();
	private Map<String, String> environmentVariables = new HashMap<String, String>();

	private Boolean logCommand = false;
	private Boolean redirectStreams = true;
	private Boolean exceptionOnFailed = true;
	private Boolean mergeEnvironmentVariables = true;

//	private Authentication authentication;

	private String osConsole = null;
	private String generateScript = null;

	/** 24 hours */
	private Long watchdogTimeout = 24 * 60 * 60 * 1000l;

	private TestResult testResult;

	private ExecutionResources executionResources;

	/** Sudo the command, as root if empty or as user if not. */
	private String sudo = null;
	// TODO make it more secure and robust, test only once
	private final String sudoPrompt = UUID.randomUUID().toString();
	private String askPassProgram = "/usr/libexec/openssh/ssh-askpass";
	@SuppressWarnings("unused")
	private boolean firstLine = true;
	@SuppressWarnings("unused")
	private CallbackHandler callbackHandler;
	/** Chroot to the this path (must not be empty) */
	private String chroot = null;

	// Current
	/** Current watchdog, null if process is completed */
	ExecuteWatchdog currentWatchdog = null;

	/** Empty constructor */
	public SystemCall() {

	}

	/**
	 * Constructor based on the provided command list.
	 * 
	 * @param command the command list
	 */
	public SystemCall(List<Object> command) {
		this.command = command;
	}

	/**
	 * Constructor based on the provided command.
	 * 
	 * @param cmd the command. If the provided string contains no space a command
	 *            list is initialized with the argument as first component (useful
	 *            for chained construction)
	 */
	public SystemCall(String cmd) {
		if (cmd.indexOf(' ') < 0) {
			command = new ArrayList<Object>();
			command.add(cmd);
		} else {
			this.cmd = cmd;
		}
	}

	/** Executes the system call. */
	public void run() {
//		authentication = SecurityContextHolder.getContext().getAuthentication();

		// Manage streams
		Writer stdOutWriter = null;
		OutputStream stdOutputStream = null;
		Writer stdErrWriter = null;
		InputStream stdInStream = null;
		if (stdOutFile != null)
			if (redirectStdOut)
				stdOutputStream = createOutputStream(stdOutFile);
			else
				stdOutWriter = createWriter(stdOutFile, true);

		if (stdErrFile != null) {
			stdErrWriter = createWriter(stdErrFile, true);
		} else {
			if (stdOutFile != null && !redirectStdOut)
				stdErrWriter = createWriter(stdOutFile, true);
		}

		try {
			if (stdInFile != null)
				stdInStream = Files.newInputStream(stdInFile);
			else {
				stdInStream = new PipedInputStream();
				stdInSink = new PipedOutputStream((PipedInputStream) stdInStream);
			}
		} catch (IOException e2) {
			throw new SlcException("Cannot open a stream for " + stdInFile, e2);
		}

		logger.log(TRACE, () -> "os.name=" + System.getProperty("os.name"));
		logger.log(TRACE, () -> "os.arch=" + System.getProperty("os.arch"));
		logger.log(TRACE, () -> "os.version=" + System.getProperty("os.version"));

		// Execution directory
		File dir = new File(getExecDirToUse());
		// if (!dir.exists())
		// dir.mkdirs();

		// Watchdog to check for lost processes
		Executor executorToUse;
		if (executor != null)
			executorToUse = executor;
		else
			executorToUse = new DefaultExecutor();
		executorToUse.setWatchdog(createWatchdog());

		if (redirectStreams) {
			// Redirect standard streams
			executorToUse.setStreamHandler(
					createExecuteStreamHandler(stdOutWriter, stdOutputStream, stdErrWriter, stdInStream));
		} else {
			// Dummy stream handler (otherwise pump is used)
			executorToUse.setStreamHandler(new DummyexecuteStreamHandler());
		}

		executorToUse.setProcessDestroyer(new ShutdownHookProcessDestroyer());
		executorToUse.setWorkingDirectory(dir);

		// Command line to use
		final CommandLine commandLine = createCommandLine();
		if (logCommand)
			logger.log(INFO, "Execute command:\n" + commandLine + "\n in working directory: \n" + dir + "\n");

		// Env variables
		Map<String, String> environmentVariablesToUse = null;
		environmentVariablesToUse = new HashMap<String, String>();
		if (mergeEnvironmentVariables)
			environmentVariablesToUse.putAll(System.getenv());
		if (environmentVariables.size() > 0)
			environmentVariablesToUse.putAll(environmentVariables);

		// Execute
		ExecuteResultHandler executeResultHandler = createExecuteResultHandler(commandLine);

		//
		// THE EXECUTION PROPER
		//
		try {
			if (synchronous)
				try {
					int exitValue = executorToUse.execute(commandLine, environmentVariablesToUse);
					executeResultHandler.onProcessComplete(exitValue);
				} catch (ExecuteException e1) {
					if (e1.getExitValue() == Executor.INVALID_EXITVALUE) {
						Thread.currentThread().interrupt();
						return;
					}
					// Sleep 1s in order to make sure error logs are flushed
					Thread.sleep(1000);
					executeResultHandler.onProcessFailed(e1);
				}
			else
				executorToUse.execute(commandLine, environmentVariablesToUse, executeResultHandler);
		} catch (SlcException e) {
			throw e;
		} catch (Exception e) {
			throw new SlcException("Could not execute command " + commandLine, e);
		} finally {
			IOUtils.closeQuietly(stdOutWriter);
			IOUtils.closeQuietly(stdErrWriter);
			IOUtils.closeQuietly(stdInStream);
			IOUtils.closeQuietly(stdInSink);
		}

	}

	public synchronized String function() {
		final StringBuffer buf = new StringBuffer("");
		SystemCallOutputListener tempOutputListener = new SystemCallOutputListener() {
			private Long lineCount = 0l;

			public void newLine(SystemCall systemCall, String line, Boolean isError) {
				if (!isError) {
					if (lineCount != 0l)
						buf.append('\n');
					buf.append(line);
					lineCount++;
				}
			}
		};
		addOutputListener(tempOutputListener);
		run();
		removeOutputListener(tempOutputListener);
		return buf.toString();
	}

	public String asCommand() {
		return createCommandLine().toString();
	}

	@Override
	public String toString() {
		return asCommand();
	}

	/**
	 * Build a command line based on the properties. Can be overridden by specific
	 * command wrappers.
	 */
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
			throw new SlcException("Specify the command either as a line or as a list.");
		else if (cmdToUse != null) {
			if (chroot != null && !chroot.trim().equals(""))
				cmdToUse = "chroot \"" + chroot + "\" " + cmdToUse;
			if (sudo != null) {
				environmentVariables.put("SUDO_ASKPASS", askPassProgram);
				if (!sudo.trim().equals(""))
					cmdToUse = "sudo -p " + sudoPrompt + " -u " + sudo + " " + cmdToUse;
				else
					cmdToUse = "sudo -p " + sudoPrompt + " " + cmdToUse;
			}

			// GENERATE COMMAND LINE
			commandLine = CommandLine.parse(cmdToUse);
		} else if (commandToUse != null) {
			if (commandToUse.size() == 0)
				throw new SlcException("Command line is empty.");

			if (chroot != null && sudo != null) {
				commandToUse.add(0, "chroot");
				commandToUse.add(1, chroot);
			}

			if (sudo != null) {
				environmentVariables.put("SUDO_ASKPASS", askPassProgram);
				commandToUse.add(0, "sudo");
				commandToUse.add(1, "-p");
				commandToUse.add(2, sudoPrompt);
				if (!sudo.trim().equals("")) {
					commandToUse.add(3, "-u");
					commandToUse.add(4, sudo);
				}
			}

			// GENERATE COMMAND LINE
			commandLine = new CommandLine(commandToUse.get(0).toString());

			for (int i = 1; i < commandToUse.size(); i++) {
				if (logger.isLoggable(TRACE))
					logger.log(TRACE, commandToUse.get(i));
				commandLine.addArgument(commandToUse.get(i).toString());
			}
		} else {
			// all cases covered previously
			throw new UnsupportedException();
		}

		if (generateScript != null) {
			File scriptFile = new File(getExecDirToUse() + File.separator + generateScript);
			try {
				FileUtils.writeStringToFile(scriptFile,
						(osConsole != null ? osConsole + " " : "") + commandLine.toString());
			} catch (IOException e) {
				throw new SlcException("Could not generate script " + scriptFile, e);
			}
			commandLine = new CommandLine(scriptFile);
		} else {
			if (osConsole != null)
				commandLine = CommandLine.parse(osConsole + " " + commandLine.toString());
		}

		return commandLine;
	}

	/**
	 * Creates a {@link PumpStreamHandler} which redirects streams to the custom
	 * logging mechanism.
	 */
	protected ExecuteStreamHandler createExecuteStreamHandler(final Writer stdOutWriter,
			final OutputStream stdOutputStream, final Writer stdErrWriter, final InputStream stdInStream) {

		// Log writers
		OutputStream stdout = stdOutputStream != null ? stdOutputStream : new LogOutputStream() {
			protected void processLine(String line, int level) {
				// if (firstLine) {
				// if (sudo != null && callbackHandler != null
				// && line.startsWith(sudoPrompt)) {
				// try {
				// PasswordCallback pc = new PasswordCallback(
				// "sudo password", false);
				// Callback[] cbs = { pc };
				// callbackHandler.handle(cbs);
				// char[] pwd = pc.getPassword();
				// char[] arr = Arrays.copyOf(pwd,
				// pwd.length + 1);
				// arr[arr.length - 1] = '\n';
				// IOUtils.write(arr, stdInSink);
				// stdInSink.flush();
				// } catch (Exception e) {
				// throw new SlcException(
				// "Cannot retrieve sudo password", e);
				// }
				// }
				// firstLine = false;
				// }

				if (line != null && !line.trim().equals(""))
					logStdOut(line);

				if (stdOutWriter != null)
					appendLineToFile(stdOutWriter, line);
			}
		};

		OutputStream stderr = new LogOutputStream() {
			protected void processLine(String line, int level) {
				if (line != null && !line.trim().equals(""))
					logStdErr(line);
				if (stdErrWriter != null)
					appendLineToFile(stdErrWriter, line);
			}
		};

		PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(stdout, stderr, stdInStream) {

			@Override
			public void stop() throws IOException {
				// prevents the method to block when joining stdin
				if (stdInSink != null)
					IOUtils.closeQuietly(stdInSink);

				super.stop();
			}
		};
		return pumpStreamHandler;
	}

	/** Creates the default {@link ExecuteResultHandler}. */
	protected ExecuteResultHandler createExecuteResultHandler(final CommandLine commandLine) {
		return new ExecuteResultHandler() {

			public void onProcessComplete(int exitValue) {
				String msg = "System call '" + commandLine + "' properly completed.";
				logger.log(TRACE, () -> msg);
				if (testResult != null) {
					forwardPath(testResult);
					testResult.addResultPart(new SimpleResultPart(TestStatus.PASSED, msg));
				}
				releaseWatchdog();
			}

			public void onProcessFailed(ExecuteException e) {

				String msg = "System call '" + commandLine + "' failed.";
				if (testResult != null) {
					forwardPath(testResult);
					testResult.addResultPart(new SimpleResultPart(TestStatus.ERROR, msg, e));
				} else {
					if (exceptionOnFailed)
						throw new SlcException(msg, e);
					else
						logger.log(ERROR, msg, e);
				}
				releaseWatchdog();
			}
		};
	}

	@Deprecated
	protected void forwardPath(TestResult testResult) {
		// TODO: allocate a TreeSPath
	}

	/**
	 * Shortcut method getting the execDir to use
	 */
	protected String getExecDirToUse() {
		try {
			if (execDir != null) {
				return execDir;
			}
			return System.getProperty("user.dir");
		} catch (Exception e) {
			throw new SlcException("Cannot find exec dir", e);
		}
	}

	protected void logStdOut(String line) {
		for (SystemCallOutputListener outputListener : outputListeners)
			outputListener.newLine(this, line, false);
		log(stdOutLogLevel, line);
	}

	protected void logStdErr(String line) {
		for (SystemCallOutputListener outputListener : outputListeners)
			outputListener.newLine(this, line, true);
		log(stdErrLogLevel, line);
	}

	/** Log from the underlying streams. */
	protected void log(String logLevel, String line) {
		// TODO optimize
//		if (SecurityContextHolder.getContext().getAuthentication() == null) {
//			SecurityContextHolder.getContext()
//					.setAuthentication(authentication);
//		}

		if ("ERROR".equals(logLevel))
			logger.log(ERROR, line);
		else if ("WARN".equals(logLevel))
			logger.log(WARNING, line);
		else if ("WARNING".equals(logLevel))
			logger.log(WARNING, line);
		else if ("INFO".equals(logLevel))
			logger.log(INFO, line);
		else if ("DEBUG".equals(logLevel))
			logger.log(DEBUG, line);
		else if ("TRACE".equals(logLevel))
			logger.log(TRACE, line);
		else if (LOG_STDOUT.equals(logLevel))
			System.out.println(line);
		else if ("System.err".equals(logLevel))
			System.err.println(line);
		else
			throw new SlcException("Unknown log level " + logLevel);
	}

	/** Append line to a log file. */
	protected void appendLineToFile(Writer writer, String line) {
		try {
			writer.append(line).append('\n');
		} catch (IOException e) {
			logger.log(ERROR, "Cannot write to log file", e);
		}
	}

	/** Creates the writer for the output/err files. */
	protected Writer createWriter(Path target, Boolean append) {
		FileWriter writer = null;
		try {

			final File file;
			if (executionResources != null)
				file = new File(executionResources.getAsOsPath(target, true));
			else
				file = target.toFile();
			writer = new FileWriter(file, append);
		} catch (IOException e) {
			logger.log(ERROR, "Cannot get file for " + target, e);
			IOUtils.closeQuietly(writer);
		}
		return writer;
	}

	/** Creates an outputstream for the output/err files. */
	protected OutputStream createOutputStream(Path target) {
		FileOutputStream out = null;
		try {

			final File file;
			if (executionResources != null)
				file = new File(executionResources.getAsOsPath(target, true));
			else
				file = target.toFile();
			out = new FileOutputStream(file, false);
		} catch (IOException e) {
			logger.log(ERROR, "Cannot get file for " + target, e);
			IOUtils.closeQuietly(out);
		}
		return out;
	}

	/** Append the argument (for chaining) */
	public SystemCall arg(String arg) {
		if (command == null)
			command = new ArrayList<Object>();
		command.add(arg);
		return this;
	}

	/** Append the argument (for chaining) */
	public SystemCall arg(String arg, String value) {
		if (command == null)
			command = new ArrayList<Object>();
		command.add(arg);
		command.add(value);
		return this;
	}

	// CONTROL
	public synchronized Boolean isRunning() {
		return currentWatchdog != null;
	}

	private synchronized ExecuteWatchdog createWatchdog() {
//		if (currentWatchdog != null)
//			throw new SlcException("A process is already being monitored");
		currentWatchdog = new ExecuteWatchdog(watchdogTimeout);
		return currentWatchdog;
	}

	private synchronized void releaseWatchdog() {
		currentWatchdog = null;
	}

	public synchronized void kill() {
		if (currentWatchdog != null)
			currentWatchdog.destroyProcess();
	}

	/** */
	public void setCmd(String command) {
		this.cmd = command;
	}

	public void setCommand(List<Object> command) {
		this.command = command;
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

	public void setOsCommands(Map<String, List<Object>> osCommands) {
		this.osCommands = osCommands;
	}

	public void setOsCmds(Map<String, String> osCmds) {
		this.osCmds = osCmds;
	}

	public void setEnvironmentVariables(Map<String, String> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}

	public Map<String, String> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setWatchdogTimeout(Long watchdogTimeout) {
		this.watchdogTimeout = watchdogTimeout;
	}

	public void setStdOutFile(Path stdOutFile) {
		this.stdOutFile = stdOutFile;
	}

	public void setStdErrFile(Path stdErrFile) {
		this.stdErrFile = stdErrFile;
	}

	public void setStdInFile(Path stdInFile) {
		this.stdInFile = stdInFile;
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

	public void setExceptionOnFailed(Boolean exceptionOnFailed) {
		this.exceptionOnFailed = exceptionOnFailed;
	}

	public void setMergeEnvironmentVariables(Boolean mergeEnvironmentVariables) {
		this.mergeEnvironmentVariables = mergeEnvironmentVariables;
	}

	public void setOsConsole(String osConsole) {
		this.osConsole = osConsole;
	}

	public void setGenerateScript(String generateScript) {
		this.generateScript = generateScript;
	}

	public void setExecutionResources(ExecutionResources executionResources) {
		this.executionResources = executionResources;
	}

	public void setRedirectStdOut(Boolean redirectStdOut) {
		this.redirectStdOut = redirectStdOut;
	}

	public void addOutputListener(SystemCallOutputListener outputListener) {
		outputListeners.add(outputListener);
	}

	public void removeOutputListener(SystemCallOutputListener outputListener) {
		outputListeners.remove(outputListener);
	}

	public void setOutputListeners(List<SystemCallOutputListener> outputListeners) {
		this.outputListeners = outputListeners;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public void setSudo(String sudo) {
		this.sudo = sudo;
	}

	public void setCallbackHandler(CallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

	public void setChroot(String chroot) {
		this.chroot = chroot;
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
