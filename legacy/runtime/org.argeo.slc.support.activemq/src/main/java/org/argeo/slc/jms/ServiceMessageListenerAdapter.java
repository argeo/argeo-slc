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
package org.argeo.slc.jms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.argeo.slc.SlcException;
import org.argeo.slc.msg.ExecutionAnswer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

public class ServiceMessageListenerAdapter extends MessageListenerAdapter {
	public final static String DEFAULT_METHOD_NAME_PARAMETER = "action";
	public final static String BODY_ARGUMENT = "BODY";

	private Map<String, List<String>> methodArguments = new HashMap<String, List<String>>();

	private static String methodNameParameter = DEFAULT_METHOD_NAME_PARAMETER;

	@Override
	protected Object extractMessage(Message message) throws JMSException {
		return new ExtractedMessage(message);
	}

	@Override
	protected String getListenerMethodName(Message originalMessage,
			Object extractedMessage) throws JMSException {
		return ((ExtractedMessage) extractedMessage).methodName;
	}

	@Override
	protected Object[] buildListenerArguments(Object extractedMessage) {
		return ((ExtractedMessage) extractedMessage).arguments;
	}

	@Override
	public void onMessage(Message message, Session session) throws JMSException {
		try {// hacked and simplified from parent class
			// Regular case: find a handler method reflectively.
			Object convertedMessage = extractMessage(message);
			String methodName = getListenerMethodName(message, convertedMessage);

			// Invoke the handler method with appropriate arguments.
			Object[] listenerArguments = buildListenerArguments(convertedMessage);
			Object result = invokeListenerMethod(methodName, listenerArguments);
			if (result != null) {
				handleResult(result, message, session);
			} else {
				ExecutionAnswer answer = ExecutionAnswer.ok("Execution of "
						+ methodName + " on " + getDelegate() + " succeeeded.");
				Message okMessage = getMessageConverter().toMessage(answer,
						session);
				sendResponse(session, getResponseDestination(message,
						okMessage, session), okMessage);
			}
		} catch (Exception e) {
			if (session == null)
				return;

			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			ExecutionAnswer answer = ExecutionAnswer.error(writer.toString());
			Message errorMessage = getMessageConverter().toMessage(answer,
					session);
			sendResponse(session, getResponseDestination(message, errorMessage,
					session), errorMessage);
		}
	}

	protected class ExtractedMessage {
		private final String methodName;
		private final Object[] arguments;
//		private final Message originalMessage;

		public ExtractedMessage(Message originalMessage) throws JMSException {
//			this.originalMessage = originalMessage;

			if (!originalMessage.propertyExists(methodNameParameter))
				throw new SlcException("No property " + methodNameParameter
						+ " in incoming message,"
						+ " cannot determine service method");

			methodName = originalMessage.getStringProperty(methodNameParameter);

			if (!methodArguments.containsKey(methodName)) {// no arg specified
				arguments = new Object[0];
			} else {
				List<String> parameterNames = methodArguments.get(methodName);
				List<Object> arguments = new ArrayList<Object>();
				int count = 0;
				for (String name : parameterNames) {
					if (name.equals(BODY_ARGUMENT)) {
						Object body = getMessageConverter().fromMessage(
								originalMessage);
						arguments.add(body);
					} else {
						if (!originalMessage.propertyExists(name))
							throw new SlcException("No property " + name
									+ " in incoming message,"
									+ " cannot determine argument #" + count);
						arguments.add(originalMessage.getObjectProperty(name));
					}
					count++;
				}
				this.arguments = arguments.toArray();
			}
		}
	}

	public void setMethodArguments(Map<String, List<String>> methodArguments) {
		this.methodArguments = methodArguments;
	}

	public static void setMethodNameParameter(String methodNameParameter) {
		ServiceMessageListenerAdapter.methodNameParameter = methodNameParameter;
	}
	
	
}
