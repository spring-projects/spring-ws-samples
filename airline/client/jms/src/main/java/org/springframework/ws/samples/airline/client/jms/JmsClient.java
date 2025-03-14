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

package org.springframework.ws.samples.airline.client.jms;

import javax.xml.transform.stream.StreamResult;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.xml.transform.StringSource;

/**
 * @author Arjen Poutsma
 */
public class JmsClient extends WebServiceGatewaySupport {

	private static final String PAYLOAD = "<airline:GetFlightsRequest xmlns:airline=\"http://www.springframework.org/spring-ws/samples/airline/schemas/messages\">"
			+ "<airline:from>AMS</airline:from>" + "<airline:to>VCE</airline:to>"
			+ "<airline:departureDate>2006-01-31</airline:departureDate>" + "</airline:GetFlightsRequest>";

	public void getFlights() {
		getWebServiceTemplate().sendSourceAndReceiveToResult(new StringSource(PAYLOAD), new StreamResult(System.out));
	}

}
