package org.springframework.ws.samples.echo.client.saaj;

import java.net.MalformedURLException;

import javax.xml.soap.SOAPException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaajEchoClient {

	public static void main(String[] args) throws MalformedURLException, SOAPException {
		SpringApplication.run(SaajEchoClient.class);

		String url = "http://localhost:8080/echo-server/services";
		if (args.length > 0) {
			url = args[0];
		}

		EchoClient echoClient = new EchoClient(url);
		echoClient.callWebService();
	}
}
