/*
 * Copyright 2025-present the original author or authors.
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

package org.springframework.ws.samples.mtom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.samples.mtom.schema.LoadContentRequest;
import org.springframework.ws.samples.mtom.schema.LoadContentResponse;
import org.springframework.ws.samples.mtom.schema.ObjectFactory;
import org.springframework.ws.samples.mtom.schema.StoreContentRequest;
import org.springframework.ws.samples.mtom.schema.StoreContentResponse;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * Integration tests that invoke the MTOM server over HTTP, as a real client would do.
 * Content that is not sent as an MTOM attachment would be read as an empty stream, so
 * these tests compare the actual bytes.
 *
 * @author Stephane Nicoll
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MtomServerTests {

	@TempDir
	static Path storageDirectory;

	@DynamicPropertySource
	static void storageProperties(DynamicPropertyRegistry registry) {
		registry.add("mtom.storage-directory", () -> storageDirectory.toString());
	}

	private final ObjectFactory objectFactory = new ObjectFactory();

	@LocalServerPort
	private int port;

	private WebServiceTemplate template;

	@BeforeEach
	void setUp() throws Exception {
		SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
		messageFactory.afterPropertiesSet();
		this.template = new WebServiceTemplate(messageFactory);
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath(ObjectFactory.class.getPackageName());
		marshaller.setMtomEnabled(true);
		marshaller.afterPropertiesSet();
		this.template.setMarshaller(marshaller);
		this.template.setUnmarshaller(marshaller);
		this.template.setDefaultUri("http://localhost:" + this.port + "/mtom-server/services");
	}

	@Test
	void storeContentWritesTheAttachment(@TempDir Path source) throws IOException {
		byte[] content = randomBytes(256 * 1024);
		StoreContentRequest request = this.objectFactory.createStoreContentRequest();
		request.setName("store");
		request.setContent(dataHandler(source, content));

		StoreContentResponse response = (StoreContentResponse) this.template.marshalSendAndReceive(request);
		assertThat(response.getMessage()).isEqualTo("Success");
		assertSameContent(Files.readAllBytes(storageDirectory.resolve("store.tmp")), content);
	}

	@Test
	void loadContentReturnsTheAttachment() throws IOException {
		byte[] content = randomBytes(256 * 1024);
		Files.write(storageDirectory.resolve("load.tmp"), content);
		LoadContentRequest request = this.objectFactory.createLoadContentRequest();
		request.setName("load");

		LoadContentResponse response = (LoadContentResponse) this.template.marshalSendAndReceive(request);
		assertThat(response.getName()).isEqualTo("load");
		try (InputStream in = response.getContent().getInputStream()) {
			assertSameContent(in.readAllBytes(), content);
		}
	}

	@Test
	void loadUnknownContentReturnsClientFault() {
		LoadContentRequest request = this.objectFactory.createLoadContentRequest();
		request.setName("does-not-exist");

		assertThatExceptionOfType(SoapFaultClientException.class)
			.isThrownBy(() -> this.template.marshalSendAndReceive(request))
			.withMessageContaining("does-not-exist");
	}

	@Test
	void storeContentCannotEscapeTheStorageDirectory(@TempDir Path source) throws IOException {
		StoreContentRequest request = this.objectFactory.createStoreContentRequest();
		request.setName("../escaped");
		request.setContent(dataHandler(source, randomBytes(16)));

		assertThatExceptionOfType(SoapFaultClientException.class)
			.isThrownBy(() -> this.template.marshalSendAndReceive(request))
			.withMessageContaining("Invalid content name");
		assertThat(storageDirectory.resolveSibling("escaped.tmp")).doesNotExist();
	}

	private static DataHandler dataHandler(Path directory, byte[] content) throws IOException {
		Path file = Files.write(directory.resolve("content.bin"), content);
		return new DataHandler(new FileDataSource(file.toFile()));
	}

	private static void assertSameContent(byte[] actual, byte[] expected) {
		assertThat(actual.length).as("Content size").isEqualTo(expected.length);
		assertThat(Arrays.equals(actual, expected)).as("Same content").isTrue();
	}

	private static byte[] randomBytes(int size) {
		byte[] bytes = new byte[size];
		new Random(42).nextBytes(bytes);
		return bytes;
	}

}
