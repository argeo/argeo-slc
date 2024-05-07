package org.argeo.slc.cms.deploy.osgi;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.argeo.api.a2.A2Source;
import org.argeo.api.a2.FsA2Source;
import org.argeo.api.init.InitConstants;
import org.argeo.api.init.RuntimeContext;
import org.argeo.api.slc.WellKnownConstants;
import org.argeo.api.slc.build.Distribution;
import org.argeo.api.slc.deploy.DeployedSystem;
import org.argeo.api.slc.deploy.Deployment;
import org.argeo.api.slc.deploy.DeploymentData;
import org.argeo.api.slc.deploy.TargetData;
import org.argeo.init.osgi.OsgiRuntimeContext;
import org.argeo.slc.cms.deploy.CmsDeployedSystem;
import org.argeo.slc.cms.deploy.CmsDeploymentData;
import org.argeo.slc.cms.deploy.CmsTargetData;
import org.argeo.slc.cms.deploy.SimpleCmsDeploymentData;
import org.argeo.slc.cms.distribution.A2Distribution;

/** The process of deploying an OSGi based Argeo CMS system. */
public class OsgiCmsDeployment implements Deployment {
	private final static Logger logger = System.getLogger(OsgiCmsDeployment.class.getName());

	private A2Distribution distribution;
	private CmsTargetData targetData;
	private CmsDeploymentData deploymentData;

	private CmsDeployedSystem deployedSystem;

	private OsgiRuntimeContext runtimeContext;

	@Override
	public void run() {
		try {
			Map<String, String> config = new TreeMap<>();

			// sources
			StringJoiner sourcesProperty = new StringJoiner(",");
			for (A2Source a2Source : distribution.getA2Sources()) {
				sourcesProperty.add(a2Source.getUri().toString());
			}
			config.put(InitConstants.PROP_ARGEO_OSGI_SOURCES, sourcesProperty.toString());

			// target
			config.put(WellKnownConstants.OSGI_INSTANCE_AREA,
					targetData.getInstanceData().toRealPath().toUri().toString());

			if (targetData.getHost() != null) {
				config.put("argeo.host", targetData.getHost().toString());
			}

			if (targetData.getHttpPort() != null) {
				config.put("argeo.http.port", targetData.getHttpPort().toString());
			}

			Path configurationArea = Files.createTempDirectory("slc-cms-test");
			config.put(WellKnownConstants.OSGI_CONFIGURATION_AREA, configurationArea.toUri().toString());

			// modules activation
			for (int startLevel = 0; startLevel <= 6; startLevel++) {
				List<String> modules = deploymentData.getModulesToActivate(startLevel);
				if (modules.size() != 0) {
					String startProperty = String.join(",", modules);
					config.put(InitConstants.PROP_ARGEO_OSGI_START + "." + startLevel, startProperty);
				}
			}

			config.put("org.eclipse.equinox.http.jetty.autostart", "false");
			config.put("org.osgi.framework.system.packages.extra",
					"sun.security.util,sun.security.internal.spec,sun.security.provider,com.sun.net.httpserver,com.sun.jndi.ldap,com.sun.jndi.ldap.sasl,com.sun.jndi.dns,com.sun.security.jgss,com.sun.nio.file,com.sun.nio.sctp");
			config.put("eclipse.ignoreApp", "true");
			config.put("osgi.noShutdown", "true");
			config.put("osgi.clean", "true");
			config.put("osgi.framework.useSystemProperties", "false");

			config.put("argeo.directory", "dc=example,dc=com.ldif");

			if (targetData instanceof OsgiCmsTargetData osgiCmsTargetData
					&& osgiCmsTargetData.getTelnetPort() != null) {
				String hostStr = "";
				if (targetData.getHost() != null) {
					hostStr = targetData.getHost().toString() + ":";
				}
				config.put("osgi.console", hostStr + osgiCmsTargetData.getTelnetPort().toString());
			}

			// initialise
			for (String key : config.keySet()) {
//				System.out.println(key + "=" + config.get(key));
				logger.log(Level.TRACE, () -> key + "=" + config.get(key));
			}

			// FIXME use runtime manager
			runtimeContext = new OsgiRuntimeContext(null, config);
			runtimeContext.run();

//			deployedSystem = new OsgiCmsDeployedSystem(runtimeContext.getFramework().getBundleContext(), distribution,
//					targetData, deploymentData);

		} catch (Exception e) {
			throw new IllegalStateException("Cannot run OSGi deployment", e);
		}

	}

	@Override
	public DeployedSystem getDeployedSystem() {
		return deployedSystem;
	}

	@Override
	public void setTargetData(TargetData targetData) {
		this.targetData = (CmsTargetData) targetData;
	}

	@Override
	public void setDeploymentData(DeploymentData deploymentData) {
		this.deploymentData = (CmsDeploymentData) deploymentData;
	}

	@Override
	public void setDistribution(Distribution distribution) {
		this.distribution = (A2Distribution) distribution;
	}

	public OsgiRuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	public static void main(String[] args) {
		RuntimeContext runtimeContext = test();
		try {
			runtimeContext.waitForStop(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static RuntimeContext test() {
		try {
			Path userHome = Paths.get(System.getProperty("user.home"));

			// distribution
			Path a2Base = userHome.resolve("dev/git/unstable/output/a2");
			A2Distribution distribution = new A2Distribution();
			Map<String, String> xOr = new HashMap<>();
			xOr.put("osgi", "equinox");
			xOr.put("swt", "rap");
			xOr.put("log", "syslogger");
			xOr.put("crypto", "fips");
			distribution.getA2Sources().add(new FsA2Source(a2Base, xOr, true, null, null));

			// target data
			Path instanceData = userHome.resolve("dev/git/unstable/argeo-slc/sdk/exec/cms-deployment/data");
			Files.createDirectories(instanceData);
			OsgiCmsTargetData targetData = new OsgiCmsTargetData(instanceData, "host1", 7070, 2323);

			// deployment data
			SimpleCmsDeploymentData deploymentData = new SimpleCmsDeploymentData();
			deploymentData.getModulesToActivate(2).add("org.eclipse.equinox.http.servlet");
			deploymentData.getModulesToActivate(2).add("org.apache.felix.scr");
			deploymentData.getModulesToActivate(2).add("org.eclipse.rap.rwt.osgi");
			deploymentData.getModulesToActivate(2).add("org.eclipse.equinox.console");

			deploymentData.getModulesToActivate(3).add("org.argeo.cms");
			deploymentData.getModulesToActivate(3).add("org.argeo.cms.ee");
			deploymentData.getModulesToActivate(3).add("org.argeo.cms.lib.sshd");
			deploymentData.getModulesToActivate(3).add("org.argeo.cms.lib.equinox");
			deploymentData.getModulesToActivate(3).add("org.argeo.cms.lib.jetty");
			deploymentData.getModulesToActivate(3).add("org.argeo.cms.swt.rap");

			deploymentData.getModulesToActivate(4).add("org.argeo.cms.jcr");
			deploymentData.getModulesToActivate(4).add("org.argeo.app.profile.acr.fs");

			deploymentData.getModulesToActivate(5).add("org.argeo.app.core");
			deploymentData.getModulesToActivate(5).add("org.argeo.app.ui");
			deploymentData.getModulesToActivate(5).add("org.argeo.app.theme.default");
			deploymentData.getModulesToActivate(5).add("org.argeo.app.jcr");

			deploymentData.getModulesToActivate(5).add("org.example.suite.theme");
			deploymentData.getModulesToActivate(5).add("org.example.suite.core");
			deploymentData.getModulesToActivate(5).add("org.example.suite.ui");
			deploymentData.getModulesToActivate(5).add("org.example.suite.ui.rap");

			OsgiCmsDeployment deployment = new OsgiCmsDeployment();
			deployment.setDistribution(distribution);
			deployment.setTargetData(targetData);
			deployment.setDeploymentData(deploymentData);
			deployment.run();

			boolean multiple = true;
			if (multiple) {
				// wait a bit
//				Thread.sleep(5000);

				Path instanceData2 = userHome.resolve("dev/git/unstable/argeo-slc/sdk/exec/cms-deployment2/data");
				Files.createDirectories(instanceData2);
				OsgiCmsTargetData targetData2 = new OsgiCmsTargetData(instanceData2, "host2", 7070, 2323);

				OsgiCmsDeployment deployment2 = new OsgiCmsDeployment();
				deployment2.setDistribution(distribution);
				deployment2.setTargetData(targetData2);
				deployment2.setDeploymentData(deploymentData);
				deployment2.run();
			}

			return deployment.getRuntimeContext();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

}
