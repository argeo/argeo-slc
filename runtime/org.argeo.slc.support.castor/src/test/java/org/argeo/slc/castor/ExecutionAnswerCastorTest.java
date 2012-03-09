/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
