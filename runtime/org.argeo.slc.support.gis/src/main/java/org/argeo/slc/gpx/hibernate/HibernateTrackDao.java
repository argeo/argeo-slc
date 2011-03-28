package org.argeo.slc.gpx.hibernate;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.slc.gpx.TrackDao;
import org.argeo.slc.gpx.TrackPoint;
import org.argeo.slc.gpx.TrackSegment;
import org.argeo.slc.gpx.TrackSpeed;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.hibernatespatial.HBSpatialExtension;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Parses a GPX track file and import the data
 * 
 * On PostGIS, a useful index is:
 * <code>CREATE INDEX track_speeds_line ON track_speeds USING GIST (line)</code>
 */
public class HibernateTrackDao extends HibernateDaoSupport implements TrackDao {
	static {
		// TODO: put in in init method and make it configurable
		HBSpatialExtension.getConfiguration();
	}

	private final static Log log = LogFactory.getLog(HibernateTrackDao.class);
	private final static DateFormat ISO8601 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");

	private Long batchSize = 100l;
	private Integer targetSrid = 4326;
	private Float maxSpeed = 200f;

	private GeodeticCalculator geodeticCalculator;

	public Object importTrackPoints(String source, String sensor, InputStream in) {
		long begin = System.currentTimeMillis();
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			SAXParser sp = spf.newSAXParser();
			InputSource input = new InputSource(in);
			geodeticCalculator = new GeodeticCalculator(CRS.decode("EPSG:"
					+ targetSrid));
			TrackGpxHandler handler = new TrackGpxHandler(sensor, targetSrid);
			sp.parse(input, handler);
			return null;
		} catch (Exception e) {
			throw new ArgeoException("Cannot parse GPX stream", e);
		} finally {
			long duration = System.currentTimeMillis() - begin;
			if (log.isDebugEnabled())
				log.debug("Imported " + source + " from sensor '" + sensor
						+ "' in " + (duration) + " ms");
		}
	}

	protected void processTrackSegment(TrackSegment trackSegment,
			GeometryFactory geometryFactory) {
		TrackSpeed currentTrackSpeed = null;
		List<Coordinate> coords = new ArrayList<Coordinate>();
		trackPoints: for (int i = 0; i < trackSegment.getTrackPoints().size(); i++) {
			TrackPoint trackPoint = trackSegment.getTrackPoints().get(i);
			coords.add(new Coordinate(trackPoint.getLocation().getX(),
					trackPoint.getLocation().getY()));

			if (i == 0)
				trackSegment.setStartUtc(trackPoint.getUtcTimestamp());

			if (i == trackSegment.getTrackPoints().size() - 1)
				trackSegment.setEndUtc(trackPoint.getUtcTimestamp());
			else {
				// order 1 coefficients (speed)
				TrackPoint next = trackSegment.getTrackPoints().get(i + 1);

				Coordinate[] crds = { trackPoint.getLocation().getCoordinate(),
						next.getLocation().getCoordinate() };
				LineString line = geometryFactory.createLineString(crds);
				Long duration = next.getUtcTimestamp().getTime()
						- trackPoint.getUtcTimestamp().getTime();
				if (duration < 0) {
					log.warn("Duration " + duration + " is negative between "
							+ trackPoint.getLocation() + " and "
							+ next.getLocation()
							+ ", skipping speed computation");
					currentTrackSpeed = null;
					continue trackPoints;
				}
				TrackSpeed trackSpeed = new TrackSpeed(trackPoint, line,
						duration, geodeticCalculator);
				if (trackSpeed.getSpeed() > maxSpeed) {
					log.warn("Speed " + trackSpeed.getSpeed() + " is above "
							+ maxSpeed + " between " + trackPoint.getLocation()
							+ " and " + next.getLocation()
							+ ", skipping speed computation");
					currentTrackSpeed = null;
					continue trackPoints;
				}

				// order 2 coefficients (acceleration, azimuth variation)
				if (currentTrackSpeed != null) {
					// compute acceleration (in m/s²)
					Double speed1 = trackSpeed.getDistance()
							/ (trackSpeed.getDuration() / 1000);
					Double speed2 = currentTrackSpeed.getDistance()
							/ (currentTrackSpeed.getDuration() / 1000);
					Double acceleration = (speed1 - speed2)
							/ (currentTrackSpeed.getDuration() / 1000);
					trackSpeed.setAcceleration(acceleration);

					Double azimuthVariation = convertAzimuth(trackSpeed
							.getAzimuth())
							- convertAzimuth(currentTrackSpeed.getAzimuth());
					if (azimuthVariation > 180)
						azimuthVariation = -(360 - azimuthVariation);
					else if (azimuthVariation < -180)
						azimuthVariation = (360 + azimuthVariation);
					trackSpeed.setAzimuthVariation(azimuthVariation);
				}
				trackSegment.getTrackSpeeds().add(trackSpeed);
				currentTrackSpeed = trackSpeed;
			}

		}
		LineString segment = geometryFactory.createLineString(coords
				.toArray(new Coordinate[coords.size()]));
		trackSegment.setSegment(segment);

	}

	/** Normalize from [-180°,180°] to [0°,360°] */
	private Double convertAzimuth(Double azimuth) {
		if (azimuth < 0)
			return 360d + azimuth;
		else
			return azimuth;
	}

	public void setBatchSize(Long batchSize) {
		this.batchSize = batchSize;
	}

	public void setTargetSrid(Integer targetSrid) {
		this.targetSrid = targetSrid;
	}

	public void setMaxSpeed(Float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	class TrackGpxHandler extends DefaultHandler {
		private final CoordinateReferenceSystem wgs84;
		private final GeometryFactory targetGF;

		private final MathTransform reprojection;

		public static final String TAG_TRKSEG = "trkseg";
		public static final String TAG_TRKPT = "trkpt";
		public static final String TAG_TIME = "time";
		public static final String TAG_ELE = "ele";
		public static final String ATTR_LAT = "lat";
		public static final String ATTR_LON = "lon";

		private String sensor;

		private StringBuffer accumulator = new StringBuffer();

		private Long segmentCount = 0l;
		private Long pointCount = 0l;
		private Long currentSegmentPointCount = 0l;
		private String currentSegmentUuid = null;
		private TrackPoint currentTrackPoint = null;

		private TrackSegment currentTrackSegment = null;

		private List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();

		public TrackGpxHandler(String sensor, Integer srid) {
			this.sensor = sensor;
			PrecisionModel precisionModel = new PrecisionModel();
			targetGF = new GeometryFactory(precisionModel, srid);
			if (srid != 4326) {
				try {
					wgs84 = CRS.decode("EPSG:4326");
					reprojection = CRS.findMathTransform(wgs84,
							CRS.decode("EPSG:" + srid));
				} catch (Exception e) {
					throw new ArgeoException("Cannot find reprojection", e);
				}
			} else {
				reprojection = null;
				wgs84 = null;
			}
		}

		public void characters(char[] buffer, int start, int length) {
			accumulator.append(buffer, start, length);
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// log.debug("Element: localName=" + localName + ", uri=" + uri
			// + ", qName=" + qName);

			accumulator.setLength(0); // Ready to accumulate new text
			if (qName.equals(TAG_TRKSEG)) {
				currentSegmentUuid = UUID.randomUUID().toString();
				currentTrackSegment = new TrackSegment();
				currentTrackSegment.setSensor(sensor);
				currentTrackSegment.setUuid(currentSegmentUuid);
			} else if (qName.equals(TAG_TRKPT)) {
				currentTrackPoint = new TrackPoint();
				currentTrackPoint.setSensor(sensor);
				currentTrackPoint.setSegmentUuid(currentSegmentUuid);
				String latStr = attributes.getValue(ATTR_LAT);
				String lonStr = attributes.getValue(ATTR_LON);
				Coordinate coordinate = new Coordinate(
						Double.parseDouble(lonStr), Double.parseDouble(latStr));
				Point location = reproject(coordinate);
				currentTrackPoint.setLocation(location);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// if (log.isDebugEnabled())
			// log.debug("TAG " + qName);
			if (qName.equals(TAG_TRKSEG)) {
				// if (log.isDebugEnabled())
				// log.debug("Processed segment " + currentSegmentUuid + ": "
				// + currentSegmentPointCount + " points");

				if (currentTrackSegment.getTrackPoints().size() > 1) {
					processTrackSegment(currentTrackSegment, targetGF);

					// persist
					getHibernateTemplate().save(currentTrackSegment);
					segmentCount++;
					if (segmentCount % batchSize == 0) {
						getHibernateTemplate().flush();
						getHibernateTemplate().clear();
					}
				} else if (currentTrackSegment.getTrackPoints().size() == 1) {
					TrackPoint trackPoint = currentTrackSegment
							.getTrackPoints().get(0);
					getHibernateTemplate().save(trackPoint);
				}
				currentSegmentPointCount = 0l;
				currentSegmentUuid = null;
			} else if (qName.equals(TAG_TRKPT)) {
				trackPoints.add(currentTrackPoint);
				pointCount++;
				currentSegmentPointCount++;

				currentTrackSegment.getTrackPoints().add(currentTrackPoint);

				// getHibernateTemplate().save(currentTrackPoint);

				currentTrackPoint = null;
			} else if (qName.equals(TAG_ELE)) {
				Double elevation = Double.parseDouble(accumulator.toString()
						.trim());
				currentTrackPoint.setElevation(elevation);
			} else if (qName.equals(TAG_TIME) && currentTrackPoint != null) {
				String timeStr = accumulator.toString().trim();
				try {
					Date time = ISO8601.parse(timeStr);
					currentTrackPoint.setUtcTimestamp(time);
				} catch (ParseException e) {
					throw new ArgeoException("Cannot parse date " + timeStr);
				}
			}

		}

		protected Point reproject(Coordinate coordinate) {
			if (reprojection != null) {
				try {
					// invert order
					DirectPosition2D pos = new DirectPosition2D(wgs84,
							coordinate.y, coordinate.x);
					DirectPosition targetPos = reprojection
							.transform(pos, null);
					Coordinate targetCoordinate = new Coordinate(
							targetPos.getOrdinate(0), targetPos.getOrdinate(1));
					return targetGF.createPoint(targetCoordinate);
				} catch (Exception e) {
					throw new ArgeoException("Cannot reproject " + coordinate,
							e);
				}
			} else {
				return targetGF.createPoint(coordinate);
			}
		}
	}

}
