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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import jakarta.activation.DataHandler;
import jakarta.activation.URLDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StopWatch;

/**
 * Stores an image on the MTOM server and loads it back.
 *
 * @author Tareq Abed Rabbo
 * @author Arjen Poutsma
 */
@SpringBootApplication
public class MtomClientApplication {

	private static final Logger logger = LoggerFactory.getLogger(MtomClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MtomClientApplication.class, args);
	}

	@Bean
	CommandLineRunner invoke(SaajMtomClient client) {
		return args -> {
			StopWatch stopWatch = new StopWatch("MtomClient");

			stopWatch.start("store");
			String message = client.storeContent("spring-ws-logo",
					new URLDataSource(new ClassPathResource("spring-ws-logo.png").getURL()));
			stopWatch.stop();
			logger.info("Content stored: {}", message);

			stopWatch.start("load");
			DataHandler content = client.loadContent("spring-ws-logo");
			Path out = Files.createTempFile("spring-ws-mtom-", ".bin");
			long size = copy(content, out);
			stopWatch.stop();

			logger.info("Received file size [bytes]: {}", size);
			logger.info("Stored at {}", out);
			logger.info(stopWatch.prettyPrint());
		};
	}

	private static long copy(DataHandler content, Path target) throws IOException {
		try (InputStream in = content.getInputStream()) {
			return Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}

}
