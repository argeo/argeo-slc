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

package org.argeo.slc.gpsbabel;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.gis.model.FieldPosition;
import org.argeo.slc.jts.PositionProvider;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GpsBabelPositionProvider implements PositionProvider {
	private Log log = LogFactory.getLog(GpsBabelPositionProvider.class);

	private GpsBabelCall gpsBabelCall;

	private GeometryFactory geometryFactory = new GeometryFactory();

	private String inputFormat = "garmin,get_posn";
	private String inputFile = "usb:";

	private Boolean silentlyFailing = false;

	public void init() {
		gpsBabelCall = new GpsBabelCall(inputFormat, inputFile, "csv", "-");
	}

	public FieldPosition currentPosition() {
		// lazy init
		if (gpsBabelCall == null)
			init();

		String output;
		try {
			output = gpsBabelCall.function();
			silentlyFailing = false;
		} catch (Exception e) {
			if (!silentlyFailing) {
				log.warn(e.getMessage()
						+ ": "
						+ (e.getCause() != null ? e.getCause().getMessage()
								: ""));
				if (log.isTraceEnabled())
					e.printStackTrace();
				silentlyFailing = true;
			}
			return null;
		}
		StringTokenizer st = new StringTokenizer(output, ",");
		Double latitude = Double.parseDouble(st.nextToken());
		Double longitude = Double.parseDouble(st.nextToken());
		Point position = geometryFactory.createPoint(new Coordinate(longitude,
				latitude));
		return new FieldPosition(position);
	}

	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

}
