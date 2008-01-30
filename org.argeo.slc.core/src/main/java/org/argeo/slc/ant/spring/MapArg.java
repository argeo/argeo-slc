package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

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
					map.put(key, arg.getValue());
				}
			}
		}
		return map;
	}

	public static class EntryArg {
		private String key;
		private Object value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
