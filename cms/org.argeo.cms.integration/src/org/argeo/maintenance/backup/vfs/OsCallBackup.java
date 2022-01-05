package org.argeo.maintenance.backup.vfs;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.argeo.api.cms.CmsLog;

/**
 * Runs an OS command and save its standard output as a file. Typically used for
 * MySQL or OpenLDAP dumps.
 */
public class OsCallBackup extends AbstractAtomicBackup {
	private final static CmsLog log = CmsLog.getLog(OsCallBackup.class);

	private String command;
	private Map<String, String> variables = new HashMap<String, String>();
	private Executor executor = new DefaultExecutor();

	private Map<String, String> environment = new HashMap<String, String>();

	/** Name of the sudo user, root if "", not sudo if null */
	private String sudo = null;

	public OsCallBackup() {
	}

	public OsCallBackup(String name) {
		super(name);
	}

	public OsCallBackup(String name, String command) {
		super(name);
		this.command = command;
	}

	@Override
	public void writeBackup(FileObject targetFo) {
		String commandToUse = command;

		// sudo
		if (sudo != null) {
			if (sudo.equals(""))
				commandToUse = "sudo " + commandToUse;
			else
				commandToUse = "sudo -u " + sudo + " " + commandToUse;
		}

		CommandLine commandLine = CommandLine.parse(commandToUse, variables);
		ByteArrayOutputStream errBos = new ByteArrayOutputStream();
		if (log.isTraceEnabled())
			log.trace(commandLine.toString());

		try {
			// stdout
			FileContent targetContent = targetFo.getContent();
			// stderr
			ExecuteStreamHandler streamHandler = new PumpStreamHandler(targetContent.getOutputStream(), errBos);
			executor.setStreamHandler(streamHandler);
			executor.execute(commandLine, environment);
		} catch (ExecuteException e) {
			byte[] err = errBos.toByteArray();
			String errStr = new String(err);
			throw new MaintenanceException("Process " + commandLine + " failed (" + e.getExitValue() + "): " + errStr, e);
		} catch (Exception e) {
			byte[] err = errBos.toByteArray();
			String errStr = new String(err);
			throw new MaintenanceException("Process " + commandLine + " failed: " + errStr, e);
		} finally {
			IOUtils.closeQuietly(errBos);
		}
	}

	public void setCommand(String command) {
		this.command = command;
	}

	protected String getCommand() {
		return command;
	}

	/**
	 * A reference to the environment variables that will be passed to the
	 * process. Empty by default.
	 */
	protected Map<String, String> getEnvironment() {
		return environment;
	}

	protected Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public void setSudo(String sudo) {
		this.sudo = sudo;
	}

}
