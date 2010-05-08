package org.argeo.slc.geotools;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;

public class GeoToolsUtils {
	private final static Log log = LogFactory.getLog(GeoToolsUtils.class);

	public static void debugAvailableDataStores() {
		Iterator<FileDataStoreFactorySpi> ps = FileDataStoreFinder
				.getAvailableDataStores();
		log.debug("Available datastores:");
		while (ps.hasNext()) {
			log.debug(ps.next());
		}

	}

}
