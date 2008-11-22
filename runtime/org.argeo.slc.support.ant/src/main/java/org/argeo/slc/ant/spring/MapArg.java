package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.SlcException;

public class MapArg {
	private List<EntryArg> entries = new Vector<EntryArg>();
	private Map<String, Object> map = new TreeMap<String, Object>();

	public EntryArg createEntry() {
		EntryArg arg = new EntryArg();
		entries.add(arg);
		return arg;
	}

	public Map<String, Object> getAsObjectMap(Map<String, Object> originalMap) {
		Map<String, Object> objectMap = new TreeMap<String, Object>();
		for (EntryArg arg : entries) {
			String key = arg.getKey();

			if (objectMap.containsKey(key)) {
				throw new SlcException("Key '" + key + "' already set.");
			}

			if (originalMap != null && originalMap.containsKey(key)
					&& arg.getOverrideArg() != null)
				arg.getOverrideArg().setOriginal(originalMap.get(key));

			objectMap.put(key, arg.getObject());

		}
		return objectMap;
	}

	/**
	 * Returns a cached reference if it was already called. This reference could
	 * have been modified externally and thus not anymore be in line with the
	 * configuration.
	 */
	public Map<String, Object> getMap() {
		if (map.size() == 0)
			map = getAsObjectMap(null);
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

		public Object getObject() {
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

		public OverrideArg getOverrideArg() {
			return overrideArg;
		}

	}
}
