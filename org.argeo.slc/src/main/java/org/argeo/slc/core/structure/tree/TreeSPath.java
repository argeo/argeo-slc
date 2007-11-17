package org.argeo.slc.core.structure.tree;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;

/**
 * Path for tree based <code>StructureRegistry</code> implementations.
 */
public class TreeSPath implements StructurePath, Comparable<StructurePath> {
	/** Default character to use a separator: #. */
	public static Character DEFAULT_SEPARATOR = '#';

	private TreeSPath parent;
	private String name;
	private Character separator = DEFAULT_SEPARATOR;

	/** For ORM */
	private Long tid;

	public String getAsUniqueString() {
		String parentStr = parent != null ? parent.getAsUniqueString() : "";
		return parentStr + separator + name;
	}

	public void setAsUniqueString(String str) {
		TreeSPath twin = parseToCreatePath(str, getSeparator());
		name = twin.name;
		parent = twin.parent;
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

	/** Create a path without parent. */
	public static TreeSPath createRootPath(String name) {
		TreeSPath path = new TreeSPath();
		path.parent = null;
		path.name = name;
		return path;
	}

	/** Create a child . */
	public TreeSPath createChild(String name) {
		if (name.indexOf(separator) > -1) {
			throw new SlcException("Tree path name '" + name
					+ "' contains separator character " + separator);
		}
		TreeSPath path = new TreeSPath();
		path.parent = this;
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
				currPath = createRootPath(st.nextToken());
			} else {
				currPath = currPath.createChild(st.nextToken());
			}
		}
		return currPath;
	}

	public List<TreeSPath> listChildren(StructureRegistry registry) {
		return listChildrenPaths(registry, this);
	}

	public static List<TreeSPath> listChildrenPaths(StructureRegistry registry,
			TreeSPath path) {
		List<TreeSPath> paths = new Vector<TreeSPath>();
		List<StructurePath> allPaths = registry.listPaths();
		for (StructurePath sPath : allPaths) {
			TreeSPath pathT = (TreeSPath) sPath;
			if (pathT.parent != null && pathT.parent.equals(path)) {
				paths.add(pathT);
			}
		}
		return paths;
	}

	public TreeSPath getRoot(){
		TreeSPath root = this;
		while(root.getParent()!=null){
			root = root.getParent();
		}
		return root;
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

	public int compareTo(StructurePath o) {
		return getAsUniqueString().compareTo(o.getAsUniqueString());
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

	public void setSeparator(Character separator) {
		this.separator = separator;
	}

	protected void setParent(TreeSPath parent) {
		this.parent = parent;
	}

	protected void setName(String name) {
		this.name = name;
	}

}
