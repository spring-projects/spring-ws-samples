/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.ws.samples.mtom.client.sws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * Simple client that demonstrates MTOM by invoking {@code StoreImage} and {@code LoadImage} using a WebServiceTemplate
 * and SAAJ.
 *
 * @author Tareq Abed Rabbo
 * @author Arjen Poutsma
 */
@SpringBootApplication
public class SaajMtomClient extends WebServiceGatewaySupport {

	public static void main(String[] args) {
		SpringApplication.run(SaajMtomClient.class, args);
	}

	@Bean
	CommandLineRunner invoke(SaajMtomClient saajClient) {

		return args -> {
			saajClient.storeContent();
			saajClient.loadContent();
		};
	}

	private static final Logger logger = LoggerFactory.getLogger(SaajMtomClient.class);

	private ObjectFactory objectFactory = new ObjectFactory();
	private StopWatch stopWatch = new StopWatch(ClassUtils.getShortName(getClass()));

	public SaajMtomClient(SaajSoapMessageFactory messageFactory) {
		super(messageFactory);
	}

	public void storeContent() {

		StoreContentRequest storeContentRequest = this.objectFactory.createStoreContentRequest();

		storeContentRequest.setName("spring-ws-logo");
		storeContentRequest
				.setContent(new DataHandler(Thread.currentThread().getContextClassLoader().getResource("spring-ws-logo.png")));

		this.stopWatch.start("store");

		getWebServiceTemplate().marshalSendAndReceive(storeContentRequest);

		this.stopWatch.stop();

		logger.info(this.stopWatch.prettyPrint());
	}

	public void loadContent() throws IOException {

		LoadContentRequest loadContentRequest = this.objectFactory.createLoadContentRequest();
		loadContentRequest.setName("spring-ws-logo");

		String tmpDir = System.getProperty("java.io.tmpdir");
		File out = new File(tmpDir, "spring_mtom_tmp.bin");

		long freeBefore = Runtime.getRuntime().freeMemory();

		this.stopWatch.start("load");

		LoadContentResponse loadContentResponse = (LoadContentResponse) getWebServiceTemplate()
				.marshalSendAndReceive(loadContentRequest);

		this.stopWatch.stop();

		DataHandler content = loadContentResponse.getContent();
		long freeAfter = Runtime.getRuntime().freeMemory();

		logger.info("Memory usage [kB]: " + ((freeAfter - freeBefore) / 1024));

		this.stopWatch.start("loadAttachmentContent");

		long size = saveContentToFile(content, out);

		this.stopWatch.stop();

		logger.info("Received file size [kB]: " + size);
		logger.info("Stored at " + out.getAbsolutePath());
		logger.info(this.stopWatch.prettyPrint());
	}

	private static long saveContentToFile(DataHandler content, File outFile) throws IOException {

		long size = 0;

		byte[] buffer = new byte[1024];
		try (InputStream in = content.getInputStream()) {
			try (OutputStream out = new FileOutputStream(outFile)) {
				for (int readBytes; (readBytes = in.read(buffer, 0, buffer.length)) > 0;) {
					size += readBytes;
					out.write(buffer, 0, readBytes);
				}
			}
		}

		return size;
	}
}
