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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.runtime.DefaultAgent;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.msg.ReferenceList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

/** JMS based implementation of an SLC Agent. */
public class JmsAgent extends DefaultAgent implements MessageListener {
	public final static String PROPERTY_QUERY = "query";
	public final static String QUERY_PING_ALL = "pingAll";

	private final static Log log = LogFactory.getLog(JmsAgent.class);

	private JmsTemplate jmsTemplate;
	private Destination agentRegister;
	private Destination agentUnregister;

	private Destination responseDestination;

	public void init() {
		super.init();
		try {
			jmsTemplate.convertAndSend(agentRegister, getAgentDescriptor());
			log.info("Agent #" + getAgentUuid() + " registered to "
					+ agentRegister);
		} catch (JmsException e) {
			log.warn("Could not register agent "
					+ getAgentDescriptor().getUuid()
					+ " to server: "
					+ e.getMessage()
					+ ". The agent will stay offline but will keep listening for a ping all sent by server.");
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
		}
	}

	@Override
	public void destroy() {
		try {
			jmsTemplate.convertAndSend(agentUnregister, getAgentDescriptor());
			log.info("Agent #" + getAgentUuid() + " unregistered from "
					+ agentUnregister);
		} catch (JmsException e) {
			log.warn("Could not unregister agent " + getAgentUuid() + ": "
					+ e.getMessage());
			if (log.isTraceEnabled())
				log.debug("Original error.", e);
		}
		super.destroy();
	}

	public void setAgentRegister(Destination agentRegister) {
		this.agentRegister = agentRegister;
	}

	public void setAgentUnregister(Destination agentUnregister) {
		this.agentUnregister = agentUnregister;
	}

	public String getMessageSelector() {
		String messageSelector = "slc_agentId='" + getAgentUuid() + "'";
		// if (log.isDebugEnabled())
		// log.debug("Message selector: " + messageSelector);
		return messageSelector;
	}

	public void onMessage(final Message message) {
		final String query;
		final String correlationId;
		try {
			query = message.getStringProperty(PROPERTY_QUERY);
			correlationId = message.getJMSCorrelationID();
		} catch (JMSException e1) {
			throw new SlcException("Cannot analyze incoming message " + message);
		}

		final Object response;
		final Destination destinationSend;
		if (QUERY_PING_ALL.equals(query)) {
			ReferenceList refList = (ReferenceList) convertFrom(message);
			if (!refList.getReferences().contains(getAgentUuid())) {
				response = getAgentDescriptor();
				destinationSend = agentRegister;
				log.info("Agent #" + getAgentUuid() + " registering to "
						+ agentRegister + " in reply to a " + QUERY_PING_ALL
						+ " query");
			} else {
				return;
			}
		} else {
			response = process(query, message);
			destinationSend = responseDestination;
		}

		// Send response
		if (log.isTraceEnabled())
			log.trace("About to send response " + response.getClass());
		jmsTemplate.convertAndSend(destinationSend, response,
				new MessagePostProcessor() {
					public Message postProcessMessage(Message messageToSend)
							throws JMSException {
						messageToSend.setStringProperty(PROPERTY_QUERY, query);
						messageToSend.setStringProperty(
								MsgConstants.PROPERTY_SLC_AGENT_ID,
								getAgentUuid());
						messageToSend.setJMSCorrelationID(correlationId);
						return messageToSend;
					}
				});
		if (log.isTraceEnabled())
			log.debug("Sent response to query '" + query
					+ "' with correlationId " + correlationId);
	}

	/** @return response */
	public Object process(String query, Message message) {
		try {
			if ("getExecutionModuleDescriptor".equals(query)) {
				String moduleName = message.getStringProperty("moduleName");
				String version = message.getStringProperty("version");
				return getExecutionModuleDescriptor(moduleName, version);
			} else if ("listExecutionModuleDescriptors".equals(query)) {

				List<ExecutionModuleDescriptor> lst = listExecutionModuleDescriptors();
				SlcAgentDescriptor agentDescriptorToSend = new SlcAgentDescriptor(
						getAgentDescriptor());
				agentDescriptorToSend.setModuleDescriptors(lst);
				return agentDescriptorToSend;
			} else if ("runSlcExecution".equals(query)) {
				final SlcExecution slcExecution = (SlcExecution) convertFrom(message);
				new Thread() {
					public void run() {
						process(slcExecution);
					}
				}.start();
				return ExecutionAnswer.ok("Execution started on agent "
						+ getAgentUuid());
			} else if ("ping".equals(query)) {
				return ExecutionAnswer.ok("Agent " + getAgentUuid()
						+ " is alive.");
			} else {
				throw new SlcException("Unsupported query " + query);
			}
		} catch (Exception e) {
			log.error("Processing of query " + query + " failed", e);
			return ExecutionAnswer.error(e);
		}
	}

	protected Object convertFrom(Message message) {
		try {
			return jmsTemplate.getMessageConverter().fromMessage(message);
		} catch (JMSException e) {
			throw new SlcException("Cannot convert message", e);
		}
	}

	public void setResponseDestination(Destination responseDestination) {
		this.responseDestination = responseDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}