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

package org.springframework.ws.samples.mtom.ws;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;

import java.io.File;
import java.io.IOException;

import org.springframework.util.Assert;
import org.springframework.ws.samples.mtom.schema.*;
import org.springframework.ws.samples.mtom.service.ContentRepository;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * @author Arjen Poutsma
 */
@Endpoint
public class ContentRepositoryEndpoint {

	private ContentRepository contentRepository;

	private ObjectFactory objectFactory;

	public ContentRepositoryEndpoint(ContentRepository contentRepository) {

		Assert.notNull(contentRepository, "'imageRepository' must not be null");

		this.contentRepository = contentRepository;
		this.objectFactory = new ObjectFactory();
	}

	@PayloadRoot(localPart = "StoreContentRequest", namespace = "http://www.springframework.org/spring-ws/samples/mtom")
	@ResponsePayload
	public StoreContentResponse store(@RequestPayload StoreContentRequest storeContentRequest) throws IOException {

		this.contentRepository.storeContent(storeContentRequest.getName(), storeContentRequest.getContent());
		StoreContentResponse response = this.objectFactory.createStoreContentResponse();
		response.setMessage("Success");
		return response;
	}

	@PayloadRoot(localPart = "LoadContentRequest", namespace = "http://www.springframework.org/spring-ws/samples/mtom")
	@ResponsePayload
	public LoadContentResponse load(@RequestPayload LoadContentRequest loadContentRequest) throws IOException {

		LoadContentResponse response = this.objectFactory.createLoadContentResponse();

		File contentFile = this.contentRepository.loadContent(loadContentRequest.getName());
		DataHandler dataHandler = new DataHandler(new FileDataSource(contentFile));
		response.setName(loadContentRequest.getName());
		response.setContent(dataHandler);
		return response;
	}

}
