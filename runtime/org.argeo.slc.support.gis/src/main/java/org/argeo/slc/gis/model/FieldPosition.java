package org.argeo.slc.gis.model;

import java.util.Date;

import com.vividsolutions.jts.geom.Point;

public class FieldPosition {
	private Point location;
	private Date timestamp;
	private String comment;

	public FieldPosition() {
	}

	public FieldPosition(Point location) {
		this.location = location;
		this.timestamp = new Date();
	}

	public FieldPosition(Point location, Date timestamp, String comment) {
		this.location = location;
		this.timestamp = timestamp;
		this.comment = comment;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
