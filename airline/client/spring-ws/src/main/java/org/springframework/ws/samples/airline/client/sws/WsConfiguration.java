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

package org.springframework.ws.samples.airline.client.sws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

@Configuration(proxyBeanMethods = false)
public class WsConfiguration {

	@Bean
	SoapMessageFactory messageFactory() {
		return new SaajSoapMessageFactory();
	}

	@Bean
	Jaxb2Marshaller marshaller() {

		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("org.springframework.springWs.samples.airline.schemas");
		return marshaller;
	}

	@Bean
	GetFlights getFlights(SoapMessageFactory messageFactory, Jaxb2Marshaller marshaller,
			Wss4jSecurityInterceptor securityInterceptor) {

		GetFlights getFlights = new GetFlights(messageFactory);
		getFlights.setDefaultUri("http://localhost:8080/airline-server/services");
		getFlights.setMarshaller(marshaller);
		getFlights.setUnmarshaller(marshaller);
		return getFlights;
	}

	@Bean
	GetFrequentFlyerMileage getFrequentFlyerMileage(SoapMessageFactory messageFactory, Jaxb2Marshaller marshaller,
			Wss4jSecurityInterceptor securityInterceptor) {

		GetFrequentFlyerMileage getFrequentFlyerMileage = new GetFrequentFlyerMileage(messageFactory);
		getFrequentFlyerMileage.setDefaultUri("http://localhost:8080/airline-server/services");
		getFrequentFlyerMileage.setMarshaller(marshaller);
		getFrequentFlyerMileage.setUnmarshaller(marshaller);
		getFrequentFlyerMileage.setInterceptors(new ClientInterceptor[] { securityInterceptor });
		return getFrequentFlyerMileage;
	}

	@Bean
	Wss4jSecurityInterceptor securityInterceptor() {

		Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
		securityInterceptor.setSecurementActions("UsernameToken");
		securityInterceptor.setSecurementUsername("john");
		securityInterceptor.setSecurementPassword("changeme");
		return securityInterceptor;
	}

}
