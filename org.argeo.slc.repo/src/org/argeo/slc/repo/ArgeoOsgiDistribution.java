package org.argeo.slc.repo;

import org.argeo.slc.CategorizedNameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.ModularDistribution;

/** Aether compatible OSGi distribution */
public interface ArgeoOsgiDistribution extends Distribution,
		CategorizedNameVersion, ModularDistribution {

}
