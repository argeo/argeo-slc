package org.argeo.slc.diff;

import org.argeo.slc.core.UnsupportedException;

/** A diff position within an Xml file. <b>NOT YET IMPLEMENTED</b>.*/
public class XPathDiffPosition extends DiffPosition {

	private String xPath;

	public XPathDiffPosition(RelatedFile relatedFile, String path) {
		super(relatedFile);
		xPath = path;
	}

	public int compareTo(DiffPosition dp) {
		if (!(dp instanceof XPathDiffPosition))
			throw new UnsupportedException("position", dp);

		XPathDiffPosition o = (XPathDiffPosition) dp;
		if (relatedFile.equals(o.relatedFile)) {
			return xPath.compareTo(o.xPath);
		} else {
			return relatedFile.compareTo(o.relatedFile);
		}
	}

}
