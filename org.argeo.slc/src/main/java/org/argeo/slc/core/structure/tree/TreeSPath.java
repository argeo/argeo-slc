package org.argeo.slc.core.structure.tree;

import java.util.StringTokenizer;

import org.argeo.slc.core.structure.StructurePath;

public class TreeSPath implements StructurePath {
	public static Character DEFAULT_SEPARATOR = '#';

	private TreeSPath parent;
	private String name;
	private Character separator = DEFAULT_SEPARATOR;

	public String getAsUniqueString() {
		String parentStr = parent != null ? parent.getAsUniqueString() : "";
		return parentStr + separator + name;
	}

	public Character getSeparator() {
		return separator;
	}

	public TreeSPath getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public static TreeSPath createChild(TreeSPath parent, String name) {
		TreeSPath path = new TreeSPath();
		path.parent = parent;
		path.name = name;
		return path;
	}

	public static TreeSPath createTreeSPath(String path, Character separator) {
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
	public String toString(){
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
