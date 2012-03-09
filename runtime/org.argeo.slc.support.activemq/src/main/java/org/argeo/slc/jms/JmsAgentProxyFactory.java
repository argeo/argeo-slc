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

import java.util.List;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.argeo.slc.msg.ReferenceList;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

public class JmsAgentProxyFactory implements SlcAgentFactory {
	private Destination requestDestination;
	private Destination responseDestination;
	private Destination pingAllDestination;
	private JmsTemplate jmsTemplate;

	public SlcAgent getAgent(String uuid) {
		return new JmsAgentProxy(uuid, requestDestination, responseDestination,
				jmsTemplate);
	}

	public void pingAll(List<String> activeAgentIds) {
		ReferenceList referenceList = new ReferenceList(activeAgentIds);
		jmsTemplate.convertAndSend(pingAllDestination, referenceList,
				new MessagePostProcessor() {

					public Message postProcessMessage(Message message)
							throws JMSException {
						message.setJMSCorrelationID(UUID.randomUUID()
								.toString());
						message.setStringProperty(JmsAgent.PROPERTY_QUERY,
								JmsAgent.QUERY_PING_ALL);
						return message;
					}
				});
	}

	public void setRequestDestination(Destination requestDestination) {
		this.requestDestination = requestDestination;
	}

	public void setResponseDestination(Destination responseDestination) {
		this.responseDestination = responseDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setPingAllDestination(Destination pingAllDestination) {
		this.pingAllDestination = pingAllDestination;
	}

}
