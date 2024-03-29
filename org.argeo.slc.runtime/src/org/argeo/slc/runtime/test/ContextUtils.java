package org.argeo.slc.runtime.test;

import java.util.Map;
import java.util.TreeMap;

import org.argeo.api.slc.test.TestResult;
import org.argeo.api.slc.test.TestStatus;
import org.argeo.api.slc.test.context.ContextAware;
import org.argeo.api.slc.test.context.ParentContextAware;

/** Utilities for comparing and synchronising contexts. */
public class ContextUtils {
	public static void compareReachedExpected(ContextAware contextAware, TestResult testResult) {
		for (String key : contextAware.getExpectedValues().keySet()) {

			// Compare expected values with reached ones
			Object expectedValue = contextAware.getExpectedValues().get(key);

			if (expectedValue.toString().equals(contextAware.getContextSkipFlag())) {
//				if (log.isDebugEnabled())
//					log.debug("Skipped check for key '" + key + "'");
				continue;
			}

			if (contextAware.getValues().containsKey(key)) {
				Object reachedValue = contextAware.getValues().get(key);

				if (expectedValue.equals(contextAware.getContextAnyFlag())) {
					testResult.addResultPart(
							new SimpleResultPart(TestStatus.PASSED, "Expected any value for key '" + key + "'"));
				} else if (expectedValue.equals(reachedValue)) {
					testResult.addResultPart(
							new SimpleResultPart(TestStatus.PASSED, "Values matched for key '" + key + "'"));
				} else {
					testResult.addResultPart(new SimpleResultPart(TestStatus.FAILED, "Mismatch for key '" + key
							+ "': expected '" + expectedValue + "' but reached '" + reachedValue + "'"));
				}
			} else {
				testResult.addResultPart(
						new SimpleResultPart(TestStatus.FAILED, "No value reached for key '" + key + "'"));
			}
		}
	}

	/**
	 * Makes sure that all children and sub-children of parent share the same maps
	 * for values and expected values.
	 */
	public static void synchronize(ParentContextAware parent) {
		Map<String, Object> expectedValuesCommon = new TreeMap<String, Object>(parent.getExpectedValues());
		synchronize(parent, expectedValuesCommon);
//		if (log.isDebugEnabled())
//			log.debug("Synchronized context " + parent);

	}

	private static void synchronize(ParentContextAware parent, Map<String, Object> expectedValuesCommon) {
		for (ContextAware child : parent.getChildContexts()) {
			// Values
			putNotContained(parent.getValues(), child.getValues());
			child.setValues(parent.getValues());

			// Expected Values
			// Expected values reference is not overridden: each child has its
			// own expected values map.
			overrideContained(expectedValuesCommon, child.getExpectedValues());

			// Creates a new Map in order not to disturb other context using the
			// same keys
			Map<String, Object> expectedValuesCommonChild = new TreeMap<String, Object>(expectedValuesCommon);
			putNotContained(expectedValuesCommonChild, child.getExpectedValues());

			if (child instanceof ParentContextAware) {
				// Recursive sync
				synchronize((ParentContextAware) child, expectedValuesCommonChild);
			}
		}

	}

	/**
	 * Put into common map the values from child map which are not already defined
	 * in common map.
	 */
	public static void putNotContained(Map<String, Object> commonMap, Map<String, Object> childMap) {
		for (String key : childMap.keySet()) {
			if (!commonMap.containsKey(key)) {
				commonMap.put(key, childMap.get(key));
			}
		}
	}

	/** Overrides child map values with the values already set in common map */
	public static void overrideContained(Map<String, Object> commonMap, Map<String, Object> childMap) {
		for (String key : childMap.keySet()) {
			if (commonMap.containsKey(key)) {
				childMap.put(key, commonMap.get(key));
			}
		}
	}

	/** Makes sure this cannot be instantiated. */
	private ContextUtils() {

	}
}
