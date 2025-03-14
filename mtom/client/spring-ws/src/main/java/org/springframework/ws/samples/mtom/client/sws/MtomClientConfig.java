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

package org.springframework.ws.samples.mtom.client.sws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * @author Arjen Poutsma
 */
@Configuration
public class MtomClientConfig {

	@Bean
	public SaajMtomClient saajClient(SaajSoapMessageFactory messageFactory, Jaxb2Marshaller marshaller) {

		SaajMtomClient client = new SaajMtomClient(messageFactory);
		client.setDefaultUri("http://localhost:8080/mtom-server/services");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

	@Bean
	public SaajSoapMessageFactory saajSoapMessageFactory() {
		return new SaajSoapMessageFactory();
	}

	@Bean
	public Jaxb2Marshaller marshaller() {

		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.springframework.ws.samples.mtom.client.sws");
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

}
