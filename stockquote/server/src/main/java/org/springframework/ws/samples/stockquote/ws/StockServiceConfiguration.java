package org.springframework.ws.samples.stockquote.ws;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.server.EndpointAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.soap.addressing.server.AnnotationActionEndpointMapping;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.soap.server.endpoint.interceptor.SoapEnvelopeLoggingInterceptor;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHttpHandler;
import org.springframework.ws.transport.http.WsdlDefinitionHttpHandler;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

/**
 * @author Arjen Poutsma
 */
@Configuration
@ComponentScan("org.springframework.ws.samples.stockquote.ws")
public class StockServiceConfiguration {

	@Bean
	public WebServiceMessageReceiverHttpHandler soapHandler() {
		WebServiceMessageReceiverHttpHandler soapHandler = new WebServiceMessageReceiverHttpHandler();
		soapHandler.setMessageFactory(messageFactory());
		soapHandler.setMessageReceiver(messageReceiver());
		return soapHandler;
	}

	@Bean
	public SaajSoapMessageFactory messageFactory() {
		return new SaajSoapMessageFactory();
	}

	@Bean
	public SoapMessageDispatcher messageReceiver() {
		SoapMessageDispatcher messageReceiver = new SoapMessageDispatcher();
		messageReceiver.setEndpointAdapters(
				Collections.<EndpointAdapter>singletonList(endpointAdapter()));
		messageReceiver.setEndpointMappings(
				Collections.<EndpointMapping>singletonList(endpointMapping()));
		return messageReceiver;
	}

	@Bean
	public DefaultMethodEndpointAdapter endpointAdapter() {
		return new DefaultMethodEndpointAdapter();
	}

	@Bean
	public AnnotationActionEndpointMapping endpointMapping() {
		AnnotationActionEndpointMapping endpointMapping = new AnnotationActionEndpointMapping();
		endpointMapping.setMessageSender(messageSender());
		endpointMapping.setPreInterceptors(new EndpointInterceptor[] { new SoapEnvelopeLoggingInterceptor()});
		return endpointMapping;
	}

	@Bean
	public HttpUrlConnectionMessageSender messageSender() {
		return new HttpUrlConnectionMessageSender();
	}

	@Bean
	public WsdlDefinitionHttpHandler wsdlHandler() {
		return new WsdlDefinitionHttpHandler(wsdlDefinition());
	}

	@Bean
	public SimpleWsdl11Definition wsdlDefinition() {
		return new SimpleWsdl11Definition(new ClassPathResource("/org/springframework/ws/samples/stockquote/ws/stockquote.wsdl"));
	}


}
