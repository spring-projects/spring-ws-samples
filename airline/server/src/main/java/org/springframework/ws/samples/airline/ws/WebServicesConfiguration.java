package org.springframework.ws.samples.airline.ws;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
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
	PayloadLoggingInterceptor payloadLoggingInterceptor() {
		return new PayloadLoggingInterceptor();
	}

	@Bean
	PayloadValidatingInterceptor payloadValidatingInterceptor(CommonsXsdSchemaCollection xsdSchemaCollection) {

		PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
		payloadValidatingInterceptor.setXsdSchemaCollection(xsdSchemaCollection);
		payloadValidatingInterceptor.setValidateRequest(true);
		payloadValidatingInterceptor.setValidateResponse(true);

		return payloadValidatingInterceptor;
	}

//	@Bean
//	XwsSecurityInterceptor securityInterceptor(UserDetailsService securityService) {
//
//		XwsSecurityInterceptor securityInterceptor = new XwsSecurityInterceptor();
//		securityInterceptor.setSecureResponse(false);
//		securityInterceptor.setPolicyConfiguration(
//				new ClassPathResource("org/springframework/ws/samples/airline/security/securityPolicy.xml"));
//		securityInterceptor.setCallbackHandler(springDigestPasswordValidationCallbackHandler(securityService));
//
//		return securityInterceptor;
//	}

// @Bean
// PayloadRootSmartSoapEndpointInterceptor smartSoapEndpointInterceptor(XwsSecurityInterceptor securityInterceptor) {
//
// return new PayloadRootSmartSoapEndpointInterceptor(securityInterceptor,
// "http://www.springframework.org/spring-ws/samples/airline/schemas/messages", "GetFrequentFlyerMileageRequest");
// }

//	@Bean
//	SpringDigestPasswordValidationCallbackHandler springDigestPasswordValidationCallbackHandler(
//			UserDetailsService securityService) {
//
//		SpringDigestPasswordValidationCallbackHandler handler = new SpringDigestPasswordValidationCallbackHandler();
//		handler.setUserDetailsService(securityService);
//		return handler;
//	}

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
