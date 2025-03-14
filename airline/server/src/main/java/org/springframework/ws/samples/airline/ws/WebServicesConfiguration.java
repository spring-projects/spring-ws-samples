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

package org.springframework.ws.samples.airline.ws;

import javax.security.auth.callback.CallbackHandler;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.MimeTypeUtils;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SpringSecurityPasswordValidationCallbackHandler;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadRootSmartSoapEndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

@Configuration
public class WebServicesConfiguration {

	@Bean
	WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> wsMimeMappings() {

		return factory -> {
			MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
			mappings.add("xsd", MimeTypeUtils.TEXT_XML_VALUE);
			factory.setMimeMappings(mappings);
		};
	}

	@Bean
	ServletRegistrationBean<?> webServicesRegistration(ApplicationContext ctx) {

		MessageDispatcherServlet messageDispatcherServlet = new MessageDispatcherServlet();
		messageDispatcherServlet.setApplicationContext(ctx);
		messageDispatcherServlet.setTransformWsdlLocations(true);

		return new ServletRegistrationBean<>(messageDispatcherServlet, "/airline-server/*", "*.wsdl");
	}

	@Bean
	PayloadValidatingInterceptor payloadValidatingInterceptor(CommonsXsdSchemaCollection xsdSchemaCollection) {

		PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
		payloadValidatingInterceptor.setXsdSchemaCollection(xsdSchemaCollection);
		payloadValidatingInterceptor.setValidateRequest(true);
		payloadValidatingInterceptor.setValidateResponse(true);

		return payloadValidatingInterceptor;
	}

	@Bean
	Wss4jSecurityInterceptor securityInterceptor(SpringSecurityPasswordValidationCallbackHandler handler) {

		/**
		 * <xwss:SecurityConfiguration dumpMessages="false" xmlns:xwss=
		 * "http://java.sun.com/xml/ns/xwss/config">
		 * <xwss:RequireUsernameToken passwordDigestRequired="true" nonceRequired="true"/>
		 * </xwss:SecurityConfiguration>
		 */
		Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
		securityInterceptor.setValidationActions("UsernameToken");
		securityInterceptor.setValidationCallbackHandlers(new CallbackHandler[] { handler });
		securityInterceptor.setSecureResponse(false);
		return securityInterceptor;
	}

	@Bean
	PayloadRootSmartSoapEndpointInterceptor smartSoapEndpointInterceptor(Wss4jSecurityInterceptor securityInterceptor) {
		return new PayloadRootSmartSoapEndpointInterceptor(securityInterceptor,
				"http://www.springframework.org/spring-ws/samples/airline/schemas/messages",
				"GetFrequentFlyerMileageRequest");
	}

	@Bean
	SpringSecurityPasswordValidationCallbackHandler springSecurityPasswordValidationCallbackHandler(
			UserDetailsService userDetailsService) {

		SpringSecurityPasswordValidationCallbackHandler handler = new SpringSecurityPasswordValidationCallbackHandler();
		handler.setUserDetailsService(userDetailsService);
		return handler;
	}

	@Bean
	SaajSoapMessageFactory messageFactory() {
		return new SaajSoapMessageFactory();
	}

	@Bean
	SoapMessageDispatcher dispatcher() {
		return new SoapMessageDispatcher();
	}

	@Bean
	CommonsXsdSchemaCollection commonsXsdSchemaCollection() {

		CommonsXsdSchemaCollection commonsXsdSchemaCollection = new CommonsXsdSchemaCollection();
		commonsXsdSchemaCollection.setXsds(new ClassPathResource("messages.xsd"));
		commonsXsdSchemaCollection.setInline(true);
		return commonsXsdSchemaCollection;
	}

	@Bean("services")
	DefaultWsdl11Definition airline(CommonsXsdSchemaCollection schemaCollection) {

		DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
		definition.setSchemaCollection(schemaCollection);
		definition.setPortTypeName("Airline");
		definition.setLocationUri("http://localhost:8080/airline-server/services");
		definition.setTargetNamespace("http://www.springframework.org/spring-ws/samples/airline/definitions");
		return definition;
	}

}
