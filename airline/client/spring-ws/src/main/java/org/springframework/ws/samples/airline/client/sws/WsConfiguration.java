package org.springframework.ws.samples.airline.client.sws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

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
	GetFlights getFlights(SoapMessageFactory messageFactory, Jaxb2Marshaller marshaller) {

		GetFlights getFlights = new GetFlights(messageFactory);
		getFlights.setDefaultUri("http://localhost:8080/airline-server/services");
		getFlights.setMarshaller(marshaller);
		getFlights.setUnmarshaller(marshaller);
		return getFlights;
	}

	@Bean
	GetFrequentFlyerMileage getFrequentFlyerMileage(SoapMessageFactory messageFactory) {

		GetFrequentFlyerMileage getFrequentFlyerMileage = new GetFrequentFlyerMileage(messageFactory);
		getFrequentFlyerMileage.setDefaultUri("http://localhost:8080/airline-server/services");
		return getFrequentFlyerMileage;
	}
}
