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

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;

import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.xml.transform.StringSource;

public class GetFrequentFlyerMileage extends WebServiceGatewaySupport {

	public GetFrequentFlyerMileage(WebServiceMessageFactory messageFactory) {
		super(messageFactory);
	}

	public void getFrequentFlyerMileage() {

		Source source = new StringSource(
				"<GetFrequentFlyerMileageRequest xmlns=\"http://www.springframework.org/spring-ws/samples/airline/schemas/messages\" />");

		getWebServiceTemplate().sendSourceAndReceiveToResult(source, new StreamResult(System.out));
	}

}
