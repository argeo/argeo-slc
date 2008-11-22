package org.argeo.slc.support.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.DeployedSystemManager;

public class HttpdServerManager implements DeployedSystemManager<HttpdServer> {
	private final static Log log = LogFactory.getLog(HttpdServerManager.class);

	private HttpdServer httpdServer;

	public void start() {
		runProcessAsync(createCommandLine("start"));
		log.info("Started httpd server with root "
				+ getHttpdServerTargetData().getServerRoot());
	}

	public void stop() {
		runProcessAsync(createCommandLine("stop"));
		log.info("Stopped httpd server with root "
				+ getHttpdServerTargetData().getServerRoot());
	}

	protected String[] createCommandLine(String action) {
		String httpdPath = getHttpdServerTargetData().getExecutables()
				.getExecutablePath("httpd");
		String[] cmd = { httpdPath, "-d",
				getHttpdServerTargetData().getServerRoot(), "-f",
				getHttpdServerDeploymentData().getConfigFile(), "-k", action };
		if (log.isDebugEnabled())
			log.debug("Command line: " + Arrays.asList(cmd));
		return cmd;
	}

	protected static void runProcessAsync(String... command) {
		ProcessBuilder procBuilder = new ProcessBuilder(command);
		procBuilder.redirectErrorStream(true);
		try {
			Process proc = procBuilder.start();
			final InputStream in = proc.getInputStream();
			Thread logThread = new Thread() {

				@Override
				public void run() {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					String line = null;
					try {
						while ((line = reader.readLine()) != null)
							log.info(line);
					} catch (IOException e) {
						log.error("Failed to read stdout", e);
					}
				}
			};

			logThread.start();
		} catch (IOException e) {
			throw new SlcException("Could not run command", e);
		}
	}

	public void setDeployedSystem(HttpdServer httpdServer) {
		this.httpdServer = httpdServer;
	}

	protected HttpdServerDeploymentData getHttpdServerDeploymentData() {
		return (HttpdServerDeploymentData) httpdServer.getDeploymentData();
	}

	protected HttpdServerTargetData getHttpdServerTargetData() {
		return (HttpdServerTargetData) httpdServer.getTargetData();
	}
}
