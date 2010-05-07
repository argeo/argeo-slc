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
