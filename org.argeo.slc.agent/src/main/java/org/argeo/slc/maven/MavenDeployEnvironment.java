package org.argeo.slc.maven;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.ant.AntRunner;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.deploy.DeployEnvironment;

public class MavenDeployEnvironment implements DeployEnvironment {
	private static final Log log = LogFactory
			.getLog(MavenDeployEnvironment.class);
	private MavenManager mavenManager;

	public void unpackTo(Object packg, File targetLocation,
			Map<String, String> filter) {
		File packageLocation;
		String type = null;
		String removeRootDir = "enabled";
		if (packg instanceof MavenFile) {
			packageLocation = mavenManager
					.getPackageLocation((MavenFile) packg);
			type = ((MavenFile) packg).getType();
		} else if (packg instanceof File) {
			packageLocation = (File) packg;
			// TODO: type based on extension
		} else {
			throw new SlcException("Unrecognized package type "
					+ packg.getClass());
		}
		if (log.isDebugEnabled()) {
			log.debug("Unpack " + packageLocation + " of type " + type + " to "
					+ targetLocation);
		}

		try {
			File tempDir = new File("/tmp/" + UUID.randomUUID().toString());
			tempDir.mkdirs();
			targetLocation.mkdirs();
			Properties props = new Properties();
			props.setProperty("dest", targetLocation.getAbsolutePath());
			props.setProperty("src", packageLocation.getAbsolutePath());
			props.setProperty("tempDir", tempDir.getAbsolutePath());
			props.setProperty("removeRootDir", removeRootDir);

			URL antUrl = getClass().getClassLoader().getResource(
					"org/argeo/slc/support/deploy/ant/build.xml");

			if (type == null || type.equals("zip")) {
				new AntRunner(antUrl, "deployZip", props).run();
			} else if (type.equals("tar.gz")) {
				new AntRunner(antUrl, "deployTarGz", props).run();
			} else {
				throw new SlcException("Unknow package type " + type);
			}
		} catch (SlcException e) {
			throw e;
		} catch (Exception e) {
			throw new SlcException("Cannot unpack package " + packg + " to "
					+ targetLocation, e);
		}
	}

	public void setMavenManager(MavenManager mavenManager) {
		this.mavenManager = mavenManager;
	}

}
