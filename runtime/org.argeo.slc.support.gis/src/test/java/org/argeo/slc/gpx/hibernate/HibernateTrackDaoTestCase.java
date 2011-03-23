/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.gpx.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.gpx.TrackDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.hibernatespatial.HBSpatialExtension;
import org.hibernatespatial.cfg.HSConfiguration;

public class HibernateTrackDaoTestCase extends HibernateTestCase {
	static {
		HSConfiguration config = new HSConfiguration();
		config.setDefaultDialect("org.hibernatespatial.postgis.PostgisDialect");

		HBSpatialExtension.setConfiguration(config);
	}

	public void testImport() throws Exception {
		String sensor = "mbaudier";
		File dir = new File("/home/mbaudier/ArgeoOffice/perso/gps/trips/2010");

		SortedSet<File> files = new TreeSet<File>(new Comparator<File>() {
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		TrackDao trackDao = getBean(TrackDao.class);
		long begin = System.currentTimeMillis();

		// order files
		for (File file : dir.listFiles()) {
			files.add(file);
		}

		for (File file : files) {
			if (!file.getName().endsWith(".gpx"))
				continue;
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				trackDao.importTrackPoints(file.getName(), sensor, in);
			} catch (Exception e) {
				log.warn("Could not import " + file + ": " + e.getMessage());
				throw e;
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		long duration = System.currentTimeMillis() - begin;
		if (log.isDebugEnabled())
			log.debug("Imported files from " + dir + " in "
					+ ((duration / 1000) / 60) + "min "
					+ ((duration / 1000) % 60) + "s");
	}
}
