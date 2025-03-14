/*
 * Copyright 2006-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.samples.airline.client.jms;

import jakarta.jms.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.transport.jms.JmsMessageSender;

/**
 * Java configuration for setting up a JMS-based client.
 *
 * @author Greg Turnquist
 */
@Configuration(proxyBeanMethods = false)
public class JmsConfiguration {

	@Bean
	JmsMessageSender jmsMessageSender(ConnectionFactory connectionFactory) {
		return new JmsMessageSender(connectionFactory);
	}

	@Bean
	JmsClient jmsClient(JmsMessageSender messageSender) {

		JmsClient jmsClient = new JmsClient();
		jmsClient.setDefaultUri("jms:RequestQueue");
		jmsClient.setMessageSender(messageSender);
		return jmsClient;
	}

}
