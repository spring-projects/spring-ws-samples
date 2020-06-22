package org.springframework.ws.samples.airline.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.transport.WebServiceMessageReceiver;
import org.springframework.ws.transport.jms.WebServiceMessageListener;

@Configuration
public class JmsConfiguration {

	@Bean(initMethod = "start")
	BrokerService broker() throws Exception {
		return BrokerFactory.createBroker("broker:tcp://localhost:61616?persistent=false");
	}

	@Bean
	ActiveMQConnectionFactory connectionFactory() {

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL("tcp://localhost:61616");
		return connectionFactory;
	}

	@Bean
	DefaultMessageListenerContainer containerFactory(ConnectionFactory connectionFactory,
			WebServiceMessageListener messageListener) {

		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setDestination(new ActiveMQQueue("RequestQueue"));
		container.setMessageListener(messageListener);
		return container;
	}

	@Bean
	WebServiceMessageListener messageListener(WebServiceMessageFactory messageFactory,
			WebServiceMessageReceiver messageReceiver) {

		WebServiceMessageListener messageListener = new WebServiceMessageListener();
		messageListener.setMessageFactory(messageFactory);
		messageListener.setMessageReceiver(messageReceiver);
		return messageListener;
	}
}
