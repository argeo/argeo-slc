package org.argeo.slc.core.test.tree;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.core.test.TestResultPart;

/**
 * List of <code>TestResultPart</code>. It can be used to build complex
 * <code>TestResult</code> affording the possibility to a
 * <code>TestDefinition</code> to add a list of result part under the same
 * <code>StructurePath</code>.
 * 
 * @see TreeTestResult
 */
public class PartSubList {

	/** For ORM */
	private Long tid;

	private List<TestResultPart> parts = new Vector<TestResultPart>();

	/** Gets the result parts. */
	public List<TestResultPart> getParts() {
		return parts;
	}

	/** Sets the result parts. */
	public void setParts(List<TestResultPart> parts) {
		this.parts = parts;
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

}
