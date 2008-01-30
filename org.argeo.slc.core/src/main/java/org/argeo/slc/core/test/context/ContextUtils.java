package org.argeo.slc.core.test.context;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRelated;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestStatus;

public class ContextUtils {
	private final static Log log = LogFactory.getLog(ContextUtils.class);

	public static void compareReachedExpected(ContextAware contextAware,
			TestResult testResult, TreeSRelated treeSRelated) {
		for (String key : contextAware.getExpectedValues().keySet()) {

			// Register in structure
			if (treeSRelated != null) {
				if (treeSRelated.getBasePath() != null) {
					TreeSPath path = treeSRelated.getBasePath()
							.createChild(key);
					StructureRegistry<TreeSPath> registry = treeSRelated
							.getRegistry();
					final StructureElement element = treeSRelated
							.getStructureElement(key);
					registry.register(path, element);
					if (testResult instanceof StructureAware)
						((StructureAware<TreeSPath>) testResult)
								.notifyCurrentPath(registry, path);

					if (log.isDebugEnabled())
						log.debug("Checking key " + key + " for path " + path);
				}
			}

			// Compare expected values with reached ones
			Object expectedValue = contextAware.getExpectedValues().get(key);

			if (contextAware.getValues().containsKey(key)) {
				Object reachedValue = contextAware.getValues().get(key);

				if (expectedValue.equals(reachedValue)) {
					testResult.addResultPart(new SimpleResultPart(
							TestStatus.PASSED, "Values matched for key '" + key
									+ "'"));
				} else {
					testResult.addResultPart(new SimpleResultPart(
							TestStatus.FAILED, "Mismatch for key '" + key
									+ "': expected '" + expectedValue
									+ "' but reached '" + reachedValue + "'"));
				}
			} else {
				testResult.addResultPart(new SimpleResultPart(
						TestStatus.FAILED, "No value reached for key '" + key
								+ "'"));
			}

			if (treeSRelated != null) {
				if (treeSRelated.getBasePath() != null) {
					if (testResult instanceof StructureAware) {
						((StructureAware<TreeSPath>) testResult)
								.notifyCurrentPath(treeSRelated.getRegistry(),
										treeSRelated.getBasePath());
					}
				}
			}
		}
	}

	/**
	 * Makes sure that all children and sub-children of parent share the same
	 * maps for values and expected values.
	 */
	public static void synchronize(ParentContextAware parent) {
		Map<String, Object> expectedValuesCommon = new TreeMap<String, Object>(
				parent.getExpectedValues());
		synchronize(parent, expectedValuesCommon);
		if (log.isDebugEnabled())
			log.debug("Synchonized context " + parent);

	}

	private static void synchronize(ParentContextAware parent,
			Map<String, Object> expectedValuesCommon) {
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
			Map<String, Object> expectedValuesCommonChild = new TreeMap<String, Object>(
					expectedValuesCommon);
			putNotContained(expectedValuesCommonChild, child
					.getExpectedValues());

			if (child instanceof ParentContextAware) {
				// Recursive sync
				synchronize((ParentContextAware) child,
						expectedValuesCommonChild);
			}
		}

	}

	/**
	 * Put into common map the values from child map which are not already
	 * defined in common map.
	 */
	private static void putNotContained(Map<String, Object> commonMap,
			Map<String, Object> childMap) {
		for (String key : childMap.keySet()) {
			if (!commonMap.containsKey(key)) {
				commonMap.put(key, childMap.get(key));
			}
		}
	}

	/** Overrides child map values with the values already set in common map */
	private static void overrideContained(Map<String, Object> commonMap,
			Map<String, Object> childMap) {
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
