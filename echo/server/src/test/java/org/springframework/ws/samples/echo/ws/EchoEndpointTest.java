/*
 * Copyright 2005-2010 the original author or authors.
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

package org.springframework.ws.samples.echo.ws;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ws.samples.echo.service.EchoService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class EchoEndpointTest {

	private EchoEndpoint endpoint;

	private Document requestDocument;

	private Document responseDocument;

	private EchoService echoServiceMock;

	@BeforeEach
	public void setUp() throws Exception {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		requestDocument = documentBuilder.newDocument();
		responseDocument = documentBuilder.newDocument();
		echoServiceMock = mock(EchoService.class);
		endpoint = new EchoEndpoint(echoServiceMock);
	}

	@Test
	public void testInvokeInternal() throws Exception {

		Element echoRequest = requestDocument.createElementNS(EchoEndpoint.NAMESPACE_URI,
				EchoEndpoint.ECHO_REQUEST_LOCAL_NAME);
		Text requestText = requestDocument.createTextNode("ABC");
		echoRequest.appendChild(requestText);
		when(echoServiceMock.echo("ABC")).thenReturn("DEF");

		Element echoResponse = endpoint.handleEchoRequest(echoRequest);

		assertThat(echoResponse.getNamespaceURI()).isEqualTo(EchoEndpoint.NAMESPACE_URI);
		assertThat(echoResponse.getLocalName()).isEqualTo(EchoEndpoint.ECHO_RESPONSE_LOCAL_NAME);
		assertThat(echoResponse.getChildNodes().item(0).getNodeValue()).isEqualTo("DEF");

		verify(echoServiceMock);
	}
}
