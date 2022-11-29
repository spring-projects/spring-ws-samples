/*
 * Copyright 2006 the original author or authors.
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

package org.springframework.ws.samples.echo.client.saaj;

import jakarta.xml.soap.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client for the Echo Web Service that uses SAAJ.
 *
 * @author Ben Ethridge
 * @author Arjen Poutsma
 */
public class EchoClient {

	public static final String NAMESPACE_URI = "http://www.springframework.org/spring-ws/samples/echo";

	public static final String PREFIX = "tns";

	private static final Logger logger = LoggerFactory.getLogger(EchoClient.class);

	private SOAPConnectionFactory connectionFactory;

	private MessageFactory messageFactory;

	private URL url;

	public EchoClient(String url) throws SOAPException, MalformedURLException {

		connectionFactory = SOAPConnectionFactory.newInstance();
		messageFactory = MessageFactory.newInstance();
		this.url = new URL(url);
	}

	private SOAPMessage createEchoRequest() throws SOAPException {

		SOAPMessage message = messageFactory.createMessage();
		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		Name echoRequestName = envelope.createName("echoRequest", PREFIX, NAMESPACE_URI);
		SOAPBodyElement echoRequestElement = message.getSOAPBody().addBodyElement(echoRequestName);
		echoRequestElement.setValue("Hello");
		return message;
	}

	public void callWebService() throws SOAPException {

		SOAPMessage request = createEchoRequest();
		SOAPConnection connection = connectionFactory.createConnection();
		SOAPMessage response = connection.call(request, url);
		if (!response.getSOAPBody().hasFault()) {
			writeEchoResponse(response);
		} else {
			SOAPFault fault = response.getSOAPBody().getFault();
			logger.error("Received SOAP Fault");
			logger.error("SOAP Fault Code :" + fault.getFaultCode());
			logger.error("SOAP Fault String :" + fault.getFaultString());
		}
	}

	private void writeEchoResponse(SOAPMessage message) throws SOAPException {

		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		Name echoResponseName = envelope.createName("echoResponse", PREFIX, NAMESPACE_URI);
		SOAPBodyElement echoResponseElement = (SOAPBodyElement) message.getSOAPBody().getChildElements(echoResponseName)
				.next();
		String echoValue = echoResponseElement.getTextContent();
		logger.info("Echo Response [" + echoValue + "]");
	}
}
