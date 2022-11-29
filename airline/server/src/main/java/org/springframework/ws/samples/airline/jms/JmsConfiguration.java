package org.springframework.ws.samples.airline.jms;

import jakarta.jms.ConnectionFactory;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.transport.WebServiceMessageReceiver;
import org.springframework.ws.transport.jms.WebServiceMessageListener;

@Configuration
public class JmsConfiguration {

	/**
	 * Make embedded ActiveMQ broker open to tcp connections.
	 */
	@Bean
	ArtemisConfigurationCustomizer customizer() {
		return configuration -> {
			configuration.addConnectorConfiguration("nettyConnector",
					new TransportConfiguration(NettyConnectorFactory.class.getName()));
			configuration.addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName()));
		};
	}

	/**
	 * Listen for JMS messages and route them into the SOAP-based {@link WebServiceMessageListener}.
	 */
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
