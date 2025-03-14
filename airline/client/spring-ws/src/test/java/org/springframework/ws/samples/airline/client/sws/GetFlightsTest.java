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

import static org.springframework.ws.test.client.RequestMatchers.*;
import static org.springframework.ws.test.client.ResponseCreators.*;

import java.util.Collections;
import java.util.Map;

import javax.xml.transform.Source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

/**
 * This test illustrates the use of the client-side testing API, introduced in Spring-WS
 * 2.0.
 *
 * @author Arjen Poutsma
 */
@ExtendWith(SpringExtension.class)
@Import(WsConfiguration.class)
public class GetFlightsTest {

	@Autowired
	GetFlights getFlights;

	private MockWebServiceServer mockServer;

	private static final String MESSAGES_NS = "http://www.springframework.org/spring-ws/samples/airline/schemas/messages";

	private static final String TYPES_NS = "http://www.springframework.org/spring-ws/samples/airline/schemas/types";

	@BeforeEach
	public void setUpMocks() {
		mockServer = MockWebServiceServer.createServer(getFlights);
	}

	@Test
	public void getFlights() {

		Source getFlightsRequest = new StringSource(
				"<GetFlightsRequest xmlns='" + MESSAGES_NS + "'>" + "<from>AMS</from>" + "<to>VCE</to>"
						+ "<departureDate>2006-01-31</departureDate>" + "</GetFlightsRequest>");

		String flightInfo = "<t:number>KL1653</t:number>"
				+ "<t:departureTime>2006-01-31T10:05:00.000+01:00</t:departureTime>"
				+ "<t:from><t:code>AMS</t:code><t:name>Schiphol Airport</t:name><t:city>Amsterdam</t:city></t:from>"
				+ "<t:arrivalTime>2006-01-31T12:25:00.000+01:00</t:arrivalTime>"
				+ "<t:to><t:code>VCE</t:code><t:name>Marco Polo Airport</t:name><t:city>Venice</t:city></t:to>"
				+ "<t:serviceClass>economy</t:serviceClass>";

		Source getFlightsResponse = new StringSource("<m:GetFlightsResponse xmlns:m='" + MESSAGES_NS + "' xmlns:t='"
				+ TYPES_NS + "'>" + "<m:flight>" + flightInfo + "</m:flight>" + "</m:GetFlightsResponse>");

		mockServer.expect(payload(getFlightsRequest)).andRespond(withPayload(getFlightsResponse));

		Source bookFlightResponse = new StringSource("<m:BookFlightResponse xmlns:m='" + MESSAGES_NS + "' xmlns:t='"
				+ TYPES_NS + "'>" + "<t:id>4</t:id>" + "<t:issueDate>2010-07-28</t:issueDate>"
				+ "<t:passengers><t:passenger><t:first>John</t:first><t:last>Doe</t:last></t:passenger></t:passengers>"
				+ "<t:flight>" + flightInfo + "</t:flight>" + "</m:BookFlightResponse>");

		Map<String, String> namespaces = Collections.singletonMap("m", MESSAGES_NS);

		mockServer.expect(xpath("/m:BookFlightRequest/m:flightNumber", namespaces).exists())
			.andExpect(xpath("/m:BookFlightRequest/m:departureTime", namespaces).exists())
			.andExpect(xpath("/m:BookFlightRequest/m:passengers", namespaces).exists())
			.andRespond(withPayload(bookFlightResponse));

		getFlights.getFlights();

		mockServer.verify();
	}

}
