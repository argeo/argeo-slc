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

import org.argeo.cms.CmsDeployProperty;
import org.argeo.init.a2.A2Source;
import org.argeo.init.a2.FsA2Source;
import org.argeo.init.osgi.OsgiBoot;
import org.argeo.init.osgi.OsgiRuntimeContext;
import org.argeo.slc.WellKnownConstants;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.cms.deploy.CmsDeployedSystem;
import org.argeo.slc.cms.deploy.CmsDeploymentData;
import org.argeo.slc.cms.deploy.CmsTargetData;
import org.argeo.slc.cms.deploy.SimpleCmsDeploymentData;
import org.argeo.slc.cms.deploy.SimpleCmsTargetData;
import org.argeo.slc.cms.distribution.A2Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.Deployment;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;

public class CmsOsgiDeployment implements Deployment {
	private final static Logger logger = System.getLogger(CmsOsgiDeployment.class.getName());

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
			config.put(OsgiBoot.PROP_ARGEO_OSGI_SOURCES, sourcesProperty.toString());

			// target
			config.put(WellKnownConstants.OSGI_INSTANCE_AREA,
					targetData.getInstanceData().toRealPath().toUri().toString());
			if (targetData.getHttpPort() != null) {
				config.put(CmsDeployProperty.HTTP_PORT.getProperty(), targetData.getHttpPort().toString());
			}

			Path configurationArea = Files.createTempDirectory("slc-cms-test");
			config.put(WellKnownConstants.OSGI_CONFIGURATION_AREA, configurationArea.toUri().toString());

			// modules activation
			for (int startLevel = 0; startLevel <= 6; startLevel++) {
				List<String> modules = deploymentData.getModulesToActivate(startLevel);
				if (modules.size() != 0) {
					String startProperty = String.join(",", modules);
					config.put(OsgiBoot.PROP_ARGEO_OSGI_START + "." + startLevel, startProperty);
				}
			}

			config.put("org.eclipse.equinox.http.jetty.autostart", "false");
			config.put("org.osgi.framework.bootdelegation",
					"com.sun.jndi.ldap,com.sun.jndi.ldap.sasl,com.sun.security.jgss,com.sun.jndi.dns,com.sun.nio.file,com.sun.nio.sctp");
			config.put("eclipse.ignoreApp", "true");
			config.put("osgi.noShutdown", "true");

			config.put("osgi.console", "2323");

			// initialise
			for (String key : config.keySet()) {
//				System.out.println(key + "=" + config.get(key));
				logger.log(Level.INFO, () -> key + "=" + config.get(key));
			}

			runtimeContext = new OsgiRuntimeContext(config);
			runtimeContext.run();

			deployedSystem = new CmsOsgiDeployedSystem(runtimeContext.getFramework().getBundleContext(), distribution,
					targetData, deploymentData);

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
		try {
			Path userHome = Paths.get(System.getProperty("user.home"));

			// distribution
			Path a2Base = userHome.resolve("dev/git/unstable/output/a2");
			A2Distribution distribution = new A2Distribution();
			Map<String, String> xOr = new HashMap<>();
			xOr.put("osgi", "equinox");
			xOr.put("swt", "rap");
			distribution.getA2Sources().add(new FsA2Source(a2Base, xOr, true));

			// target data
			Path instanceData = userHome.resolve("dev/git/unstable/argeo-slc/sdk/exec/cms-deployment/data");
			Files.createDirectories(instanceData);
			Integer httpPort = 7070;
			SimpleCmsTargetData targetData = new SimpleCmsTargetData(instanceData, httpPort);

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

			CmsOsgiDeployment deployment = new CmsOsgiDeployment();
			deployment.setDistribution(distribution);
			deployment.setTargetData(targetData);
			deployment.setDeploymentData(deploymentData);
			deployment.run();

			boolean multiple = false;
			if (multiple) {

				Path instanceData2 = userHome.resolve("dev/git/unstable/argeo-slc/sdk/exec/cms-deployment2/data");
				Files.createDirectories(instanceData2);
				Integer httpPort2 = 7071;
				SimpleCmsTargetData targetData2 = new SimpleCmsTargetData(instanceData2, httpPort2);

				CmsOsgiDeployment deployment2 = new CmsOsgiDeployment();
				deployment2.setDistribution(distribution);
				deployment2.setTargetData(targetData2);
				deployment2.setDeploymentData(deploymentData);
				deployment2.run();
			}

			deployment.getRuntimeContext().waitForStop(0);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
