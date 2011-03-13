package org.argeo.slc.gpx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vividsolutions.jts.geom.LineString;

public class TrackSegment {
	private Integer tid;
	private String uuid;
	private String sensor;
	private Date startUtc;
	private Date endUtc;
	private LineString segment;
	private List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();
	private List<TrackSpeed> trackSpeeds = new ArrayList<TrackSpeed>();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public List<TrackPoint> getTrackPoints() {
		return trackPoints;
	}

	public void setTrackPoints(List<TrackPoint> trackPoints) {
		this.trackPoints = trackPoints;
	}

	public Date getStartUtc() {
		return startUtc;
	}

	public void setStartUtc(Date start) {
		this.startUtc = start;
	}

	public Date getEndUtc() {
		return endUtc;
	}

	public void setEndUtc(Date end) {
		this.endUtc = end;
	}

	public LineString getSegment() {
		return segment;
	}

	public void setSegment(LineString segment) {
		this.segment = segment;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public List<TrackSpeed> getTrackSpeeds() {
		return trackSpeeds;
	}

	public void setTrackSpeeds(List<TrackSpeed> trackSpeeds) {
		this.trackSpeeds = trackSpeeds;
	}

}
