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

package org.springframework.ws.samples.echo.client.sws;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class SpringWsEchoClient {

	public static void main(String[] args) {
		SpringApplication.run(SpringWsEchoClient.class, args);
	}

	@Bean
	CommandLineRunner invoke(EchoClient echoClient) {

		return args -> {
			echoClient.setDefaultUri("http://localhost:8080/echo-server/services");
			echoClient.setRequest(new ClassPathResource("echoRequest.xml"));
			echoClient.echo();
		};
	}

}
