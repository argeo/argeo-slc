package org.argeo.slc.client.ui.dist;

import org.argeo.slc.SlcNames;
import org.osgi.framework.Constants;

/** Constants used across the application. */
public interface DistConstants {

	/*
	 * MISCEALLENEOUS
	 */
	public final static String DATE_TIME_FORMAT = "MM/dd/yyyy, HH:mm";
	public final static String DATE_FORMAT = "MM/dd/yyyy";
	// this should be directly retrieved from JCR APIs once we have solved the
	// problem of the translation of name space to shortcut, typically
	// {http://www.jcp.org/jcr/1.0} to jcr:
	public final static String JCR_IDENTIFIER = "jcr:uuid";
	public final static String JCR_MIXIN_TYPES = "jcr:mixinTypes";

	// FIXME: should be defined in SlcNames
	public final static String SLC_BUNDLE_NAME = SlcNames.SLC_
			+ Constants.BUNDLE_NAME;
	public final static String SLC_BUNDLE_LICENCE = SlcNames.SLC_
			+ "Bundle-License";
	public final static String SLC_BUNDLE_VENDOR = SlcNames.SLC_
			+ Constants.BUNDLE_VENDOR;

	public final static String SLC_BUNDLE_DESCRIPTION = SlcNames.SLC_
			+ Constants.BUNDLE_DESCRIPTION;

	public final String DEFAULT_PUBLIC_REPOSITORY_URI = "vm:///java";

}
