package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.core.SlcException;

public class MapArg {
	private List<EntryArg> entries = new Vector<EntryArg>();
	private Map<String, Object> map = new TreeMap<String, Object>();

	public EntryArg createEntry() {
		EntryArg arg = new EntryArg();
		entries.add(arg);
		return arg;
	}

	public Map<String, Object> getMap() {
		if (map.size() == 0) {
			for (EntryArg arg : entries) {
				String key = arg.getKey();
				if (map.containsKey(key)) {
					throw new SlcException("Key '" + key + "' already set.");
				} else {
					map.put(key, arg.getValueStr());
				}
			}
		}
		return map;
	}

	public static class EntryArg {
		private String key;
		private Object valueStr;
		private OverrideArg overrideArg;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Object getValueStr() {
			if (overrideArg != null) {
				return overrideArg.getObject();
			} else if (valueStr != null) {
				return valueStr;
			} else {
				throw new BuildException("Value not set.");
			}
		}

		public void setValue(String value) {
			check();
			this.valueStr = value;
		}

		public OverrideArg createOverride() {
			check();
			overrideArg = new OverrideArg();
			return overrideArg;
		}

		private void check() {
			if (valueStr != null || overrideArg != null) {
				throw new BuildException("Value already set");
			}
		}
	}
}
