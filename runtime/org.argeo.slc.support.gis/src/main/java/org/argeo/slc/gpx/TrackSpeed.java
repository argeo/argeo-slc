package org.argeo.slc.gpx;

import java.util.Date;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class TrackSpeed {
	private Integer tid;
	private String segmentUuid;
	private String sensor;
	private Date utcTimestamp;
	private Point location;
	private LineString line;
	private Double length;
	private Long duration;
	// length(line)/(duration in h)
	private Double speed;
	// can be null
	private Double acceleration;

	public TrackSpeed() {
	}

	public TrackSpeed(TrackPoint ref, LineString line, Long duration) {
		segmentUuid = ref.getSegmentUuid();
		sensor = ref.getSensor();
		utcTimestamp = ref.getUtcTimestamp();
		location = ref.getLocation();
		this.line = line;
		this.duration = duration;

		this.length = Math.abs(line.getLength());
		// in km/h
		this.speed = (this.length * 60 * 60) / this.duration;
	}

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

	public LineString getLine() {
		return line;
	}

	public void setLine(LineString line) {
		this.line = line;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public Double getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Double acceleration) {
		this.acceleration = acceleration;
	}

}
