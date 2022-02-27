package org.argeo.slc.repo.osgi;

import java.util.List;
import java.util.zip.ZipOutputStream;

/** Provides access to Java sources */
public interface SourcesProvider {
	/**
	 * Writes sources into a ZIP (or a JAR), under the same sirectory structure.
	 * 
	 * @param packages the packages to import
	 * @param out      the ZIP or JAR to write to
	 */
	public void writeSources(List<String> packages, ZipOutputStream zout);
}
