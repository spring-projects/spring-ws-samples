package org.springframework.ws.samples.mtom.config;

import java.util.Collections;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

/**
 * @author Arjen Poutsma
 */
@Configuration
public class MtomServerConfiguration {

	@Bean
	ServletRegistrationBean<?> webServicesRegistration(ApplicationContext ctx) {

		MessageDispatcherServlet messageDispatcherServlet = new MessageDispatcherServlet();
		messageDispatcherServlet.setApplicationContext(ctx);
		messageDispatcherServlet.setTransformWsdlLocations(true);

		return new ServletRegistrationBean<>(messageDispatcherServlet, "/mtom-server/*", "*.wsdl");
	}

	@Bean
	public Jaxb2Marshaller marshaller() {

		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.springframework.ws.samples.mtom.schema");
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

	@Bean
	public MarshallingPayloadMethodProcessor methodProcessor(Jaxb2Marshaller marshaller) {
		return new MarshallingPayloadMethodProcessor(marshaller);
	}

	@Bean
	DefaultMethodEndpointAdapter endpointAdapter(MarshallingPayloadMethodProcessor methodProcessor) {

		DefaultMethodEndpointAdapter adapter = new DefaultMethodEndpointAdapter();
		adapter.setMethodArgumentResolvers(Collections.singletonList(methodProcessor));
		adapter.setMethodReturnValueHandlers(Collections.singletonList(methodProcessor));
		return adapter;
	}

	@Bean
	public SimpleWsdl11Definition contentStore() {

		SimpleWsdl11Definition definition = new SimpleWsdl11Definition();
		definition.setWsdl(new ClassPathResource("/contentStore.wsdl"));
		return definition;
	}

}
