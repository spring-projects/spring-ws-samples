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

package org.springframework.ws.samples.echo.client.saaj;

import jakarta.xml.soap.SOAPException;

import java.net.MalformedURLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaajEchoClient {

	public static void main(String[] args) throws MalformedURLException, SOAPException {
		SpringApplication.run(SaajEchoClient.class);

		String url = "http://localhost:8080/echo-server/services";
		if (args.length > 0) {
			url = args[0];
		}

		EchoClient echoClient = new EchoClient(url);
		echoClient.callWebService();
	}

}
