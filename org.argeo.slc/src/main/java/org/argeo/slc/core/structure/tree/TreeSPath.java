package org.argeo.slc.core.structure.tree;

import java.util.StringTokenizer;

import org.argeo.slc.core.structure.StructurePath;

/**
 * Path for tree based <code>StructureRegistry</code> implementations.
 * 
 * @see TreeSRegistry
 */
public class TreeSPath implements StructurePath {
	/** Default character to use a separator: #. */
	public static Character DEFAULT_SEPARATOR = '#';

	private TreeSPath parent;
	private String name;
	private Character separator = DEFAULT_SEPARATOR;

	public String getAsUniqueString() {
		String parentStr = parent != null ? parent.getAsUniqueString() : "";
		return parentStr + separator + name;
	}

	/** The separator actually used by this path. */
	public Character getSeparator() {
		return separator;
	}

	/** Gets the parent path. */
	public TreeSPath getParent() {
		return parent;
	}

	/** Gets the name part of the path. */
	public String getName() {
		return name;
	}

	/** Create a child path based on a parent path and a name. */
	public static TreeSPath createChild(TreeSPath parent, String name) {
		TreeSPath path = new TreeSPath();
		path.parent = parent;
		path.name = name;
		return path;
	}

	/** Parses a string to a path. */
	public static TreeSPath parseToCreatePath(String path, Character separator) {
		StringTokenizer st = new StringTokenizer(path, Character
				.toString(separator));

		TreeSPath currPath = null;
		while (st.hasMoreTokens()) {
			if (currPath == null) {// begin
				currPath = createChild(null, st.nextToken());
			} else {
				currPath = createChild(currPath, st.nextToken());
			}
		}
		return currPath;
	}

	@Override
	public String toString() {
		return getAsUniqueString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StructurePath) {
			StructurePath path = (StructurePath) obj;
			return getAsUniqueString().equals(path.getAsUniqueString());
		}
		return false;
	}

}
