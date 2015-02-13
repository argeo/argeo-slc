package org.argeo.slc.rpmfactory;

/** A YUM compatible repository of RPM packages. */
public interface RpmRepository {
	public String getId();

	public String getUrl();

}
