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

package org.argeo.slc.geotools;

import java.io.File;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.gce.image.WorldImageReader;

public class WorldFileTest {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws Exception {
		File dir = new File("/home/mbaudier/gis/data/100501-Poehali");
		WorldImageFormat worldImageFormat = new WorldImageFormat();
		WorldImageReader worldImageReader = worldImageFormat
				.getReader(new File(dir, "500k--l36-1--(1984).gif"));
		GridCoverage2D gridCoverage2D = worldImageReader.read(null);
		gridCoverage2D.show();
	}

}
