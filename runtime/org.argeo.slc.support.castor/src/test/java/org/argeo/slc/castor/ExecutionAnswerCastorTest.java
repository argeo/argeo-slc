package org.argeo.slc.castor;

import org.argeo.slc.msg.ExecutionAnswer;

public class ExecutionAnswerCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarshOk() throws Exception {
		ExecutionAnswer answer = new ExecutionAnswer(ExecutionAnswer.OK,
				"No problem!");
		ExecutionAnswer answerUnm = marshUnmarsh(answer);
		assertExecutionAnswer(answer, answerUnm);
	}

	public void testMarshUnmarshError() throws Exception {
		ExecutionAnswer answer = new ExecutionAnswer(ExecutionAnswer.ERROR,
				"Oooops...");
		ExecutionAnswer answerUnm = marshUnmarsh(answer);
		assertExecutionAnswer(answer, answerUnm);
	}

	public static void assertExecutionAnswer(ExecutionAnswer expected,
			ExecutionAnswer reached) {
		assertEquals(expected.getStatus(), reached.getStatus());
		assertEquals(expected.getMessage(), reached.getMessage());
	}
}
