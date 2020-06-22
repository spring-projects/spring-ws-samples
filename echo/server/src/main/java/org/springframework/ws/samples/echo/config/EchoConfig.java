/*
 * Copyright 2005-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.samples.echo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

/**
 * Configuration class for the Echo sample.
 *
 * @author Arjen Poutsma
 */
@Configuration
public class EchoConfig extends WsConfigurerAdapter {

	@Bean
	public SimpleXsdSchema echoXsd() {
		return new SimpleXsdSchema(new ClassPathResource("echo.xsd"));
	}

	@Override
	public void addInterceptors(List<EndpointInterceptor> interceptors) {

		PayloadValidatingInterceptor validatingInterceptor = new PayloadValidatingInterceptor();

		validatingInterceptor.setXsdSchema(echoXsd());
		validatingInterceptor.setValidateRequest(true);
		validatingInterceptor.setValidateResponse(true);
		interceptors.add(validatingInterceptor);

		interceptors.add(new PayloadLoggingInterceptor());
	}

	@Bean("echo-server")
	public DefaultWsdl11Definition echo(SimpleXsdSchema echoXsd) {

		DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
		definition.setPortTypeName("Echo");
		definition.setLocationUri("http://localhost:8080/echo-server/services");
		definition.setSchema(echoXsd);

		return definition;
	}
}
