/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.jsch;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.ProcessDestroyer;

/** A Commons Exec executor executing remotely via SSH */
public class JschExecutor implements Executor {
	private File workingDirectory;
	private ExecuteStreamHandler streamHandler;

	private SshTarget sshTarget;

	public void setExitValue(int value) {
		// TODO Auto-generated method stub

	}

	public void setExitValues(int[] values) {
		// TODO Auto-generated method stub

	}

	public boolean isFailure(int exitValue) {
		return Executor.INVALID_EXITVALUE == exitValue;
	}

	public ExecuteStreamHandler getStreamHandler() {
		return streamHandler;
	}

	public void setStreamHandler(ExecuteStreamHandler streamHandler) {
		this.streamHandler = streamHandler;
	}

	public ExecuteWatchdog getWatchdog() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setWatchdog(ExecuteWatchdog watchDog) {
		// TODO Auto-generated method stub

	}

	public ProcessDestroyer getProcessDestroyer() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProcessDestroyer(ProcessDestroyer processDestroyer) {
		// TODO Auto-generated method stub

	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public int execute(CommandLine command) throws ExecuteException,
			IOException {
		return execute(command, (Map) null);
	}

	public int execute(CommandLine command, Map environment)
			throws ExecuteException, IOException {
		String cmd = command.toString();
		if(workingDirectory!=null)
			cmd = "cd "+workingDirectory.getPath()+" && "+cmd;
		RemoteExec remoteExec = new RemoteExec();
		remoteExec.setSshTarget(sshTarget);
		remoteExec.setStreamHandler(streamHandler);
		remoteExec.setCommand(cmd);
		if (environment != null)
			remoteExec.setEnv(environment);
		remoteExec.run();
		return remoteExec.getLastExitStatus() != null ? remoteExec
				.getLastExitStatus() : Executor.INVALID_EXITVALUE;
	}

	public void execute(CommandLine command, ExecuteResultHandler handler)
			throws ExecuteException, IOException {
		// TODO Auto-generated method stub

	}

	public void execute(CommandLine command, Map environment,
			ExecuteResultHandler handler) throws ExecuteException, IOException {

	}

	public SshTarget getSshTarget() {
		return sshTarget;
	}

	public void setSshTarget(SshTarget sshTarget) {
		this.sshTarget = sshTarget;
	}

}
