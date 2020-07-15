package org.argeo.slc.diff;

/**
 * <code>DiffMissing</code> using the XPath of the position as
 * <code>DiffKey</code>
 */
public class DiffMissingXml extends DiffMissing {

	public DiffMissingXml(XPathDiffPosition position) {
		super(position, new DiffKeyXml(position.getXPath()));
	}

	/** Implementation of <code>DiffKey</code> based on an XPath string. */
	protected static class DiffKeyXml implements DiffKey {
		private final String xPath;

		public DiffKeyXml(String xPath) {
			this.xPath = xPath;
		}

		public String getXPath() {
			return xPath;
		}

		@Override
		public String toString() {
			return xPath;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof DiffKeyXml))
				return false;
			return xPath.equals(((DiffKeyXml) obj).xPath);
		}

		@Override
		public int hashCode() {
			return xPath.hashCode();
		}

	}
}
