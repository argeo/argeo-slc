package org.argeo.slc.gpsbabel;

import java.util.StringTokenizer;

import org.argeo.slc.jts.PositionProvider;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GpsBabelPositionProvider implements PositionProvider {
	private GpsBabelCall gpsBabelCall;

	private GeometryFactory geometryFactory = new GeometryFactory();

	private String inputFormat = "garmin,get_posn";
	private String inputFile = "usb:";

	public void init() {
		gpsBabelCall = new GpsBabelCall(inputFormat, inputFile, "csv", "-");
	}

	public Point currentPosition() {
		// lazy init
		if (gpsBabelCall == null)
			init();

		String output = gpsBabelCall.function();
		StringTokenizer st = new StringTokenizer(output, ",");
		Double latitude = Double.parseDouble(st.nextToken());
		Double longitude = Double.parseDouble(st.nextToken());
		Point position = geometryFactory.createPoint(new Coordinate(longitude,
				latitude));
		return position;
	}

}
