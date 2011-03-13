package org.argeo.slc.gpx;

import java.util.Date;

import com.vividsolutions.jts.geom.Point;

public class TrackPoint {
	private Integer tid;
	private String segmentUuid;
	private String sensor;
	private Date utcTimestamp;
	private Point location;
	private Double elevation;

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public String getSegmentUuid() {
		return segmentUuid;
	}

	public void setSegmentUuid(String segmentUuid) {
		this.segmentUuid = segmentUuid;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public Date getUtcTimestamp() {
		return utcTimestamp;
	}

	public void setUtcTimestamp(Date ts) {
		this.utcTimestamp = ts;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

}
