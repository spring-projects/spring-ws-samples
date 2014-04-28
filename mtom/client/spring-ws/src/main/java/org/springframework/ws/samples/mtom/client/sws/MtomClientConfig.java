package org.springframework.ws.samples.mtom.client.sws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * @author Arjen Poutsma
 */
@Configuration
public class MtomClientConfig {

	@Bean
	public SaajMtomClient saajClient() {
		SaajMtomClient client = new SaajMtomClient(saajSoapMessageFactory());
		client.setDefaultUri("http://localhost:8080/mtom-server/services");
		client.setMarshaller(marshaller());
		client.setUnmarshaller(marshaller());
		return client;
	}

	@Bean
	public SaajSoapMessageFactory saajSoapMessageFactory() {
		return new SaajSoapMessageFactory();
	}

	@Bean
	public AxiomMtomClient axiomClient() {
		AxiomMtomClient client = new AxiomMtomClient(axiomSoapMessageFactory());
		client.setDefaultUri("http://localhost:8080/mtom-server/services");
		return client;
	}

	@Bean
	public AxiomSoapMessageFactory axiomSoapMessageFactory() {
		return new AxiomSoapMessageFactory();
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.springframework.ws.samples.mtom.client.sws");
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

}
