package org.argeo.slc.build;

/** A software license */
public interface License {
	public String getName();

	public String getUri();

	public String getLink();

	public String getText();
}
