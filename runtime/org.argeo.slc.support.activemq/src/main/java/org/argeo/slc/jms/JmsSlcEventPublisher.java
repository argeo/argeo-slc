/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.argeo.slc.SlcException;
import org.argeo.slc.msg.event.SlcEvent;
import org.argeo.slc.msg.event.SlcEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

public class JmsSlcEventPublisher implements SlcEventPublisher {
	private Destination eventsDestination;
	private JmsTemplate jmsTemplate;

	public void publish(final SlcEvent event) {
		if (jmsTemplate.getDeliveryMode() != DeliveryMode.PERSISTENT)
			throw new SlcException(
					"Delivery mode has to be persistent in order to have durable subscription");

		jmsTemplate.convertAndSend(eventsDestination, event,
				new MessagePostProcessor() {

					public Message postProcessMessage(Message message)
							throws JMSException {
						Map<String, String> headers = event.getHeaders();
						for (String key : headers.keySet()) {
							message.setStringProperty(key, headers.get(key));
						}
						return message;
					}
				});
	}

	public void setEventsDestination(Destination eventsDestination) {
		this.eventsDestination = eventsDestination;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
