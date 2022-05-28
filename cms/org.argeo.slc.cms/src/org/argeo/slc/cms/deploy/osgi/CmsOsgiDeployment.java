package org.argeo.slc.cms.deploy.osgi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.argeo.init.a2.A2Source;
import org.argeo.init.a2.FsA2Source;
import org.argeo.init.osgi.OsgiBoot;
import org.argeo.init.osgi.OsgiRuntimeContext;
import org.argeo.slc.WellKnownConstants;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.cms.deploy.CmsDeployedSystem;
import org.argeo.slc.cms.deploy.CmsTargetData;
import org.argeo.slc.cms.deploy.SimpleCmsTargetData;
import org.argeo.slc.cms.distribution.A2Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.Deployment;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;

public class CmsOsgiDeployment implements Deployment {

	private A2Distribution distribution;
	private CmsTargetData targetData;

	private CmsDeployedSystem deployedSystem;

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
			config.put(WellKnownConstants.OSGI_INSTANCE_AREA, targetData.getInstanceData().toUri().toString());
			if (targetData.getHttpPort() != null) {
				config.put(WellKnownConstants.OSGI_HTTP_PORT, targetData.getHttpPort().toString());
			}

			Path configurationArea = Files.createTempDirectory("slc-cms-test");
			config.put(WellKnownConstants.OSGI_CONFIGURATION_AREA, configurationArea.toString());

			// initialise
			OsgiRuntimeContext runtimeContext = new OsgiRuntimeContext(config);
			runtimeContext.run();

			deployedSystem = new CmsOsgiDeployedSystem(runtimeContext.getFramework().getBundleContext(), distribution,
					targetData, null);

			runtimeContext.waitForStop(0);
		} catch (IOException | InterruptedException e) {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void setDistribution(Distribution distribution) {
		this.distribution = (A2Distribution) distribution;
	}

	public static void main(String[] args) throws IOException {
		Path userHome = Paths.get(System.getProperty("user.home"));

		// distribution
		Path a2Base = userHome.resolve("dev/git/unstable/output/a2");
		A2Distribution distribution = new A2Distribution();
		distribution.getA2Sources().add(new FsA2Source(a2Base));

		// target data
		Path instanceData = userHome.resolve("dev/git/unstable/argeo-slc/sdk/exec/cms-deployment/data");
		Files.createDirectories(instanceData);
		Integer httpPort = 7070;
		SimpleCmsTargetData targetData = new SimpleCmsTargetData(instanceData, httpPort);

		CmsOsgiDeployment deployment = new CmsOsgiDeployment();
		deployment.setDistribution(distribution);
		deployment.setTargetData(targetData);
		deployment.run();
	}

}
