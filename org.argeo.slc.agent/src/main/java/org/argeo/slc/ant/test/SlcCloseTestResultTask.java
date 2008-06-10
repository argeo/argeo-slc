package org.argeo.slc.ant.test;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.ant.spring.SpringArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.test.TestResult;

/** Ant tasks closing a given result. */
public class SlcCloseTestResultTask extends SAwareTask {
	private final static Log log = LogFactory
			.getLog(SlcCloseTestResultTask.class);

	public List<SpringArg<TestResult>> results = new Vector<SpringArg<TestResult>>();

	@Override
	public void executeActions(String mode) {
		if (!mode.equals(StructureRegistry.READ)) {
			for (SpringArg<TestResult> result : results) {
				try {
					result.getBeanInstance().close();
				} catch (RuntimeException e) {
					log.error("Could not close result "
							+ (result.getBean() != null ? result.getBean()
									: result.getAntref()), e);
				}
			}
		}
	}

	public SpringArg<TestResult> createResult() {
		SpringArg<TestResult> result = new SpringArg<TestResult>();
		results.add(result);
		return result;
	}
}
