package org.argeo.slc.ant;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class DummyObject {
	private String name;
	private Long value;
	private DummyObject other;
	private List<DummyObject> children = new Vector<DummyObject>();
	private Map<String, DummyObject> map = new TreeMap<String, DummyObject>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public DummyObject getOther() {
		return other;
	}

	public void setOther(DummyObject other) {
		this.other = other;
	}

	public List<DummyObject> getChildren() {
		return children;
	}

	public void setChildren(List<DummyObject> children) {
		this.children = children;
	}

	public Map<String, DummyObject> getMap() {
		return map;
	}

	public void setMap(Map<String, DummyObject> map) {
		this.map = map;
	}

}
