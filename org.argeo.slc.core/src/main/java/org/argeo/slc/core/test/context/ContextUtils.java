package org.argeo.slc.core.test.context;

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

	/** Makes sure this cannot be instantiated. */
	private ContextUtils() {

	}
}
