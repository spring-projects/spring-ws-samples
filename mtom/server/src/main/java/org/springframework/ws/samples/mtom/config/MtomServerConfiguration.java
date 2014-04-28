package org.springframework.ws.samples.mtom.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurationSupport;
import org.springframework.ws.samples.mtom.service.ImageRepository;
import org.springframework.ws.samples.mtom.service.StubImageRepository;
import org.springframework.ws.samples.mtom.ws.ImageRepositoryEndpoint;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

/**
 * @author Arjen Poutsma
 */
@EnableWs
@Configuration
public class MtomServerConfiguration extends WsConfigurationSupport {

	@Bean
	public ImageRepository imageRepository() {
		return new StubImageRepository();
	}

	@Bean
	public ImageRepositoryEndpoint imageRepositoryEndpoint() {
		return new ImageRepositoryEndpoint(imageRepository());
	}

	@Bean
	@Override
	public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
		List<MethodArgumentResolver> argumentResolvers =
				new ArrayList<MethodArgumentResolver>();
		argumentResolvers.add(methodProcessor());

		List<MethodReturnValueHandler> returnValueHandlers =
				new ArrayList<MethodReturnValueHandler>();
		returnValueHandlers.add(methodProcessor());

		DefaultMethodEndpointAdapter adapter = new DefaultMethodEndpointAdapter();
		adapter.setMethodArgumentResolvers(argumentResolvers);
		adapter.setMethodReturnValueHandlers(returnValueHandlers);

		return adapter;
	}

	@Bean
	public MarshallingPayloadMethodProcessor methodProcessor() {
		return new MarshallingPayloadMethodProcessor(marshaller());
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.springframework.ws.samples.mtom.schema");
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

	@Bean
	public DefaultWsdl11Definition mtom() {
		DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
		definition.setSchema(schema());
		definition.setPortTypeName("ImageRepository");
		definition.setLocationUri("http://localhost:8080/mtom-server/");
		return definition;
	}

	@Bean
	public SimpleXsdSchema schema() {
		return new SimpleXsdSchema(new ClassPathResource("/schema.xsd"));
	}

}
