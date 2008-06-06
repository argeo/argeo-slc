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
	/** Default character to use a separator: /. */
	private static Character DEFAULT_SEPARATOR = '/';

	private Character separator = DEFAULT_SEPARATOR;

	private String asUniqueString;

	/** For ORM */
	private Long tid;

	public TreeSPath() {

	}

	public TreeSPath(String asUniqueString) {
		this.asUniqueString = checkAndFormatPath(asUniqueString);
	}

	public String getAsUniqueString() {
		return asUniqueString;
	}

	/**
	 * Sets all the required data from a string. <b>ATTENTION</b>: the path is
	 * not checked for performance reason. This method should be used only by
	 * ORM/OXM frameworks. Use constructor to create immutable tree structure
	 * paths.
	 */
	public void setAsUniqueString(String str) {
		this.asUniqueString = str;
	}

	/** The separator actually used by this path. */
	public Character getSeparator() {
		return separator;
	}

	/** Gets the parent path. */
	public TreeSPath getParent() {
		int lastSep = getAsUniqueString().lastIndexOf(separator);
		if (lastSep < 1) {
			return null;
		}
		String parentUniqueString = getAsUniqueString().substring(0, lastSep);
		return new TreeSPath(parentUniqueString);
	}

	/** Gets the name part of the path. */
	public String getName() {
		int lastSep = getAsUniqueString().lastIndexOf(separator);
		return getAsUniqueString().substring(lastSep + 1);
	}

	/** Create a path without parent. */
	public static TreeSPath createRootPath(String name) {
		if (name.indexOf(DEFAULT_SEPARATOR) >= 0) {
			throw new SlcException("Name cannot contain " + DEFAULT_SEPARATOR);
		}
		return new TreeSPath('/' + name);
	}

	/** Create a child . */
	public TreeSPath createChild(String name) {
		if (name.indexOf(separator) > -1) {
			throw new SlcException("Tree path name '" + name
					+ "' contains separator character " + separator);
		}
		return new TreeSPath(getAsUniqueString() + '/' + name);
	}

	/**
	 * Parses a string to a path.
	 * 
	 * @deprecated use constructor instead
	 */
	public static TreeSPath parseToCreatePath(String path) {
		return parseToCreatePath(path, DEFAULT_SEPARATOR);
	}

	protected String checkAndFormatPath(String str) {
		if (str.length() < 2) {
			throw new SlcException("Path " + str + " is not short");
		}
		if (str.charAt(0) != separator) {
			throw new SlcException("Path " + str + " have to start with "
					+ separator);
		}

		StringBuffer buf = new StringBuffer(str.length() + 5);
		StringTokenizer st = new StringTokenizer(str, separator.toString());
		while (st.hasMoreTokens()) {
			buf.append(separator).append(st.nextToken());
		}
		return buf.toString();
	}

	/**
	 * Parses a string to a path.
	 * 
	 * @deprecated use constructor instead
	 */
	public static TreeSPath parseToCreatePath(String path, Character separator) {
		return new TreeSPath(path);
	}

	/** Lists the children from a registry. */
	public List<TreeSPath> listChildren(StructureRegistry<TreeSPath> registry) {
		return listChildrenPaths(registry, this);
	}

	/** Lists the children from a given path from a registry. */
	public static List<TreeSPath> listChildrenPaths(
			StructureRegistry<TreeSPath> registry, TreeSPath path) {
		List<TreeSPath> paths = new Vector<TreeSPath>();
		List<TreeSPath> allPaths = registry.listPaths();
		for (TreeSPath pathT : allPaths) {
			if (pathT.getParent() != null && pathT.getParent().equals(path)) {
				paths.add(pathT);
			}
		}
		return paths;
	}

	/** Gets the root tree path of this path. */
	public TreeSPath getRoot() {
		TreeSPath root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	/** Depth of this path. */
	public Integer getDepth() {
		return depthImpl(this);
	}

	protected int depthImpl(TreeSPath path) {
		if (path.getParent() == null) {
			return 1;
		} else {
			return depthImpl(path.getParent()) + 1;
		}
	}

	public List<TreeSPath> getHierarchyAsList() {
		List<TreeSPath> lst = new Vector<TreeSPath>();
		addParentToList(lst, this);
		lst.add(this);
		return lst;
	}

	protected void addParentToList(List<TreeSPath> lst, TreeSPath current) {
		TreeSPath parent = current.getParent();
		if (parent != null) {
			addParentToList(lst, parent);
			lst.add(parent);
		}
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

	@Override
	public int hashCode() {
		return getAsUniqueString().hashCode();
	}

	public int compareTo(StructurePath o) {
		return getAsUniqueString().compareTo(o.getAsUniqueString());
	}

	public Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}
}
