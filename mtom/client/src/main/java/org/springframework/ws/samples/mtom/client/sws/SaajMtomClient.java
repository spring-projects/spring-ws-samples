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

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * Simple client that demonstrates MTOM by invoking {@code StoreContent} and
 * {@code LoadContent} using a WebServiceTemplate and SAAJ.
 *
 * @author Tareq Abed Rabbo
 * @author Arjen Poutsma
 */
public class SaajMtomClient extends WebServiceGatewaySupport {

	private final ObjectFactory objectFactory = new ObjectFactory();

	public SaajMtomClient(SaajSoapMessageFactory messageFactory) {
		super(messageFactory);
	}

	/**
	 * Store the given content, sent as an MTOM attachment.
	 * @param name the name of the content
	 * @param content the content to store
	 * @return the message of the server
	 */
	public String storeContent(String name, DataSource content) {
		StoreContentRequest request = this.objectFactory.createStoreContentRequest();
		request.setName(name);
		request.setContent(new DataHandler(content));
		StoreContentResponse response = (StoreContentResponse) getWebServiceTemplate().marshalSendAndReceive(request);
		return response.getMessage();
	}

	/**
	 * Load the content with the given name.
	 * @param name the name of the content
	 * @return the content, that the server sent as an MTOM attachment
	 */
	public DataHandler loadContent(String name) {
		LoadContentRequest request = this.objectFactory.createLoadContentRequest();
		request.setName(name);
		LoadContentResponse response = (LoadContentResponse) getWebServiceTemplate().marshalSendAndReceive(request);
		return response.getContent();
	}

}
