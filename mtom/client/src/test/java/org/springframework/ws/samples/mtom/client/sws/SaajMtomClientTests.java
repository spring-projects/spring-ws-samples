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

package org.springframework.ws.samples.mtom.client.sws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatcher;
import org.springframework.ws.test.client.ResponseCreator;
import org.springframework.ws.transport.TransportInputStream;
import org.springframework.xml.transform.StringSource;

/**
 * Tests for {@link SaajMtomClient} that verify the content is exchanged as MTOM
 * attachments rather than inlined in the payload.
 *
 * @author Stephane Nicoll
 */
@SpringJUnitConfig(MtomClientConfig.class)
class SaajMtomClientTests {

	private static final String NAMESPACE = "http://www.springframework.org/spring-ws/samples/mtom";

	private static final Map<String, String> NAMESPACES = Map.of("tns", NAMESPACE, "xop",
			"http://www.w3.org/2004/08/xop/include");

	@Autowired
	private SaajMtomClient client;

	private MockWebServiceServer server;

	@BeforeEach
	void setUp() {
		this.server = MockWebServiceServer.createServer(this.client);
	}

	@Test
	void storeContentSendsAttachment(@TempDir Path directory) throws IOException {
		byte[] content = randomBytes(64 * 1024);
		DataSource source = new FileDataSource(Files.write(directory.resolve("content.bin"), content).toFile());
		this.server.expect(xpath("/tns:StoreContentRequest/tns:Name", NAMESPACES).evaluatesTo("test"))
			.andExpect(xpath("/tns:StoreContentRequest/tns:Content/xop:Include", NAMESPACES).exists())
			.andExpect(singleAttachment(content))
			.andRespond(withPayload(new StringSource("<StoreContentResponse xmlns='" + NAMESPACE
					+ "'><Message>Success</Message></StoreContentResponse>")));

		assertThat(this.client.storeContent("test", source)).isEqualTo("Success");
		this.server.verify();
	}

	@Test
	void loadContentReadsAttachment() throws IOException {
		byte[] content = randomBytes(64 * 1024);
		this.server.expect(xpath("/tns:LoadContentRequest/tns:Name", NAMESPACES).evaluatesTo("test"))
			.andRespond(attachmentResponse("content-id", content));

		DataHandler loaded = this.client.loadContent("test");

		try (InputStream in = loaded.getInputStream()) {
			assertSameContent(in.readAllBytes(), content);
		}
		this.server.verify();
	}

	private static RequestMatcher singleAttachment(byte[] expected) {
		return (uri, request) -> {
			Iterator<Attachment> attachments = ((SaajSoapMessage) request).getAttachments();
			assertThat(attachments.hasNext()).as("MTOM attachment").isTrue();
			try (InputStream in = attachments.next().getInputStream()) {
				assertSameContent(in.readAllBytes(), expected);
			}
			assertThat(attachments.hasNext()).as("Additional attachment").isFalse();
		};
	}

	/**
	 * Create a response that is read from an {@code application/xop+xml} multipart
	 * message, as it would be received from the server.
	 */
	private static ResponseCreator attachmentResponse(String contentId, byte[] content) {
		return (uri, request, messageFactory) -> {
			String boundary = "mtom-test-boundary";
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(("--" + boundary + "\r\n"
					+ "Content-Type: application/xop+xml; charset=UTF-8; type=\"text/xml\"\r\n"
					+ "Content-Transfer-Encoding: binary\r\n" + "Content-ID: <root>\r\n\r\n"
					+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'><SOAP-ENV:Body>"
					+ "<LoadContentResponse xmlns='" + NAMESPACE + "' xmlns:xop='" + NAMESPACES.get("xop") + "'>"
					+ "<Name>test</Name><Content><xop:Include href='cid:" + contentId + "'/></Content>"
					+ "</LoadContentResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>\r\n" + "--" + boundary + "\r\n"
					+ "Content-Type: application/octet-stream\r\n" + "Content-Transfer-Encoding: binary\r\n"
					+ "Content-ID: <" + contentId + ">\r\n\r\n")
				.getBytes(StandardCharsets.UTF_8));
			out.write(content);
			out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
			String contentType = "multipart/related; type=\"application/xop+xml\"; start=\"<root>\"; "
					+ "start-info=\"text/xml\"; boundary=\"" + boundary + "\"";
			return messageFactory.createWebServiceMessage(new TransportInputStream() {
				@Override
				public Iterator<String> getHeaderNames() {
					return List.of("Content-Type").iterator();
				}

				@Override
				public Iterator<String> getHeaders(String name) {
					return "Content-Type".equalsIgnoreCase(name) ? List.of(contentType).iterator()
							: Collections.emptyIterator();
				}

				@Override
				protected InputStream createInputStream() {
					return new ByteArrayInputStream(out.toByteArray());
				}
			});
		};
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
