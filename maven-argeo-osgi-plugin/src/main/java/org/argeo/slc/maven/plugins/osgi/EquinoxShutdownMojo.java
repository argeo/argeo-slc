package org.argeo.slc.maven.plugins.osgi;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.core.runtime.adaptor.EclipseStarter;

/** Shutdowns the Equinox runtime
 * @goal equinox-shutdown
 * */
public class EquinoxShutdownMojo extends AbstractOsgiMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			EclipseStarter.shutdown();
		} catch (Exception e) {
			throw new MojoExecutionException("Cannot shutdown OSGi runtime", e);
		}
	}

}
