package org.argeo.api.slc;

import java.util.Iterator;

/** A set of {@link NameVersion}. */
public interface ModuleSet {
	Iterator<? extends NameVersion> nameVersions();
}
