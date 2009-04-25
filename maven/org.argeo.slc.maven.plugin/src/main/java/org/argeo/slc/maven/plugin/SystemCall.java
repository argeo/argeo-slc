package org.argeo.slc.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SystemCall implements Runnable {
	private String execDir;

	private String cmd = null;
	private String[] command = null;

	private boolean synchronous = true;
	private boolean captureStdIn = false;

	public SystemCall() {

	}

	public SystemCall(String execDir, String cmd, boolean captureStdIn) {
		super();
		this.execDir = execDir;
		this.cmd = cmd;
		this.synchronous = true;
		this.captureStdIn = captureStdIn;
	}

	public SystemCall(String execDir, String[] command, boolean captureStdIn) {
		super();
		this.execDir = execDir;
		this.command = command;
		this.synchronous = true;
		this.captureStdIn = captureStdIn;
	}

	public void run() {
		try {
			// Execution directory
			File dir = null;
			if (execDir != null) {
				// Replace '/' by local file separator, for portability
				execDir.replace('/', File.separatorChar);
				dir = new File(execDir).getCanonicalFile();
			}

			final Process process;

			if (cmd != null) {
				process = Runtime.getRuntime().exec(cmd, null, dir);
			} else if (command != null) {
				process = Runtime.getRuntime().exec(command, null, dir);
				;
			} else {
				throw new RuntimeException("No command specified");
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {

				public void run() {
					if (process != null) {
						try {
							process.exitValue();
						} catch (Exception e) {
							process.destroy();
							System.err.println("Killed process " + process);
						}
					}
				}
			});

			// Manage standard streams
			StreamReaderThread stdOutThread = new StreamReaderThread(process
					.getInputStream(), false);
			stdOutThread.start();
			// TODO: understand why streams are not properly flushed
			StreamReaderThread stdErrThread = new StreamReaderThread(process
					.getInputStream(), false);
			stdErrThread.start();
			if (captureStdIn)
				new StdInThread(process.getOutputStream()).start();

			// Wait for the end of the process
			if (synchronous) {
				int exitCode = process.waitFor();
				if (exitCode != 0) {
					Thread.sleep(5000);// leave the process a chance to log
					System.err.println("Process return exit code " + exitCode);
				}
			} else {
				// asynchronous: return
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not execute command " + cmd, e);
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

	public void setCmd(String command) {
		this.cmd = command;
	}

	public void setExecDir(String execdir) {
		this.execDir = execdir;
	}

	public void setCommand(String[] command) {
		this.command = command;
	}

	protected synchronized void write(String str, boolean isErr) {
		if (isErr) {
			System.err.print(str);
			System.err.flush();
		} else {
			System.out.print(str);
			System.out.flush();
		}
		notifyAll();
	}

	protected class StreamReaderThread extends Thread {
		private final InputStream stream;
		private final boolean isErr;

		public StreamReaderThread(InputStream stream, boolean isErr) {
			this.stream = stream;
			this.isErr = isErr;
		}

		public void run() {
			// BufferedReader in = null;
			InputStreamReader isr = null;
			try {
				isr = new InputStreamReader(stream);
				int c;
				StringBuffer buf = new StringBuffer(256);
				char lastCr = '\n';
				while ((c = isr.read()) != -1) {
					char cr = (char) c;

					buf.append(cr);

					boolean write = false;
					if (lastCr == '>' && cr == ' ')
						write = true;
					else if (cr == '\n')
						write = true;

					lastCr = cr;
					if (write) {
						write(buf.toString(), isErr);
						buf = new StringBuffer(256);
					}

					// if (isErr) {
					// System.err.print(cr);
					// if (cr == '\n' || cr == '>')
					// System.err.flush();
					// } else {
					// System.out.print(cr);
					// if (cr == '\n' || cr == '>')
					// System.out.flush();
					// }
				}
				// String line = null;
				// while ((line = in.readLine()) != null) {
				// stdOutCallback(line);
				// System.out.
				// }
			} catch (IOException e) {
				// catch silently
				// because the other methods
				// to check whether the stream
				// is closed would probably
				// be to costly
			} finally {
				if (synchronous)
					IOUtils.closeQuietly(isr);
			}
		}
	}

	protected class StdInThread extends Thread {
		private final OutputStream stream;

		public StdInThread(OutputStream stream) {
			this.stream = stream;
		}

		public void run() {
			// BufferedReader in = null;
			// Writer out = null;
			InputStreamReader isr = null;
			try {
				// out = new OutputStreamWriter(stream);
				isr = new InputStreamReader(System.in);
				int c;
				while ((c = isr.read()) != -1) {
					stream.write(c);
					stream.flush();
				}

				/*
				 * in = new BufferedReader(new InputStreamReader(System.in));
				 * String line = null; while ((line = in.readLine()) != null) {
				 * out.write(line); out.write("\n"); out.flush(); }
				 */
			} catch (IOException e) {
				throw new RuntimeException("Could not write to stdin stream", e);
			} finally {
				if (synchronous) {
					IOUtils.closeQuietly(isr);
					// IOUtils.closeQuietly(in);
					// IOUtils.closeQuietly(out);
				}
			}
		}

	}
}
