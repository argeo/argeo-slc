package org.argeo.slc.client.ui.dist.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

public class DistUiHelpers implements DistConstants, SlcTypes, SlcNames {
	private final static Log log = LogFactory.getLog(DistUiHelpers.class);
	private final static DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);

	/**
	 * Returns a user-friendly label for a given jcr property name. If the
	 * corresponding mapping is not found, the input String is returned. If
	 * input String is null "(No name)" is returned
	 */
	public static String getLabelJcrName(String jcrName) {
		return (String) getLabelAndDefaultValueWidth(jcrName)[0];
	}

	/**
	 * Returns a label ( (String) object[0] )and default value width ( (int)
	 * object[1] ) for a given property name
	 */
	public static Object[] getLabelAndDefaultValueWidth(String propertyName) {
		// to avoid npe :
		if (propertyName == null)
			return new Object[] { "(No name)", 60 };

		// ArtifactId
		if (propertyName.equals(SLC_ARTIFACT + "." + SLC_ARTIFACT_ID)
				|| propertyName.equals(SLC_ARTIFACT_BASE + "."
						+ SLC_ARTIFACT_ID)
				|| propertyName.equals(SLC_ARTIFACT_VERSION_BASE + "."
						+ SLC_ARTIFACT_ID)
				|| propertyName.equals(SLC_ARTIFACT_ID)) {
			return new Object[] { "Artifact ID", 200 };
		} // GroupId
		else if (propertyName.equals(SLC_ARTIFACT + "." + SLC_GROUP_ID)
				|| propertyName.equals(SLC_ARTIFACT_BASE + "." + SLC_GROUP_ID)
				|| propertyName.equals(SLC_ARTIFACT_VERSION_BASE + "."
						+ SLC_GROUP_ID) || propertyName.equals(SLC_GROUP_ID)) {
			return new Object[] { "Group ID", 120 };
		} // Version
		else if (propertyName.equals(SLC_ARTIFACT + "." + SLC_ARTIFACT_VERSION)
				|| propertyName.equals(SLC_ARTIFACT_VERSION_BASE + "."
						+ SLC_ARTIFACT_VERSION)
				|| propertyName.equals(SLC_ARTIFACT_VERSION)) {
			return new Object[] { "Version", 60 };
		} else if (propertyName.equals(SLC_ARTIFACT + "."
				+ SLC_ARTIFACT_CLASSIFIER)
				|| propertyName.equals(SLC_ARTIFACT_CLASSIFIER)) {
			return new Object[] { "Classifier", 60 };
		} else if (propertyName.equals(SLC_ARTIFACT + "."
				+ SLC_ARTIFACT_EXTENSION)
				|| propertyName.equals(SLC_ARTIFACT_EXTENSION)) {
			return new Object[] { "Type", 40 };
		} else if (propertyName.equals(SLC_BUNDLE_ARTIFACT + "."
				+ SLC_SYMBOLIC_NAME)
				|| propertyName.equals(SLC_SYMBOLIC_NAME)) {
			return new Object[] { "Symbolic name", 180 };
		} else if (propertyName.equals(SLC_BUNDLE_ARTIFACT + "."
				+ SLC_BUNDLE_VERSION)
				|| propertyName.equals(SLC_BUNDLE_VERSION)) {
			return new Object[] { "Bundle version", 120 };
		} else if (propertyName
				.equals(SLC_BUNDLE_ARTIFACT + "." + SLC_MANIFEST)
				|| propertyName.equals(SLC_MANIFEST)) {
			return new Object[] { "Manifest", 60 };
		} // TODO remove hard coded strings
		else if (propertyName.equals("slc:Bundle-ManifestVersion")) {
			return new Object[] { "Bundle Manifest Version", 60 };
		} else if (propertyName.equals("slc:Manifest-Version")) {
			return new Object[] { "Manifest Version", 60 };
		} else if (propertyName.equals("slc:Bundle-Vendor")) {
			return new Object[] { "Bundle Vendor", 60 };
		} else if (propertyName.equals("slc:Bundle-SymbolicName")) {
			return new Object[] { "Bundle symbolic name", 60 };
		} else if (propertyName.equals("slc:Bundle-Name")) {
			return new Object[] { "Bundle name", 60 };
		} else if (propertyName.equals("slc:Bundle-DocURL")) {
			return new Object[] { "Doc URL", 120 };
		} else if (propertyName.equals("slc:Bundle-Licence")) {
			return new Object[] { "Bundle licence", 120 };
		} else if (propertyName.equals(SLC_ARTIFACT_VERSION_BASE + "."
				+ JCR_IDENTIFIER)) {
			return new Object[] { "UUID", 0 };
		} else {
			if (log.isTraceEnabled())
				log.trace("No Column label provider defined for property: ["
						+ propertyName + "]");
			return new Object[] { propertyName, 60 };
		}
	}

	public static String formatValueAsString(Value value) {
		try {
			String strValue;

			if (value.getType() == PropertyType.BINARY)
				strValue = "<binary>";
			else if (value.getType() == PropertyType.DATE)
				strValue = df.format(value.getDate().getTime());
			else
				strValue = value.getString();
			return strValue;
		} catch (RepositoryException e) {
			throw new ArgeoException("unexpected error while formatting value",
					e);
		}
	}

	public static String formatAsString(Object value) {
		String strValue;
		if (value instanceof Calendar)
			strValue = df.format(((Calendar) value).getTime());
		else
			strValue = value.toString();
		return strValue;
	}
}
