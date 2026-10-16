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

package org.springframework.ws.samples.mtom.service;

import jakarta.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.ws.samples.mtom.config.MtomServerProperties;

/**
 * A {@link ContentRepository} that keeps the content in files of a local directory.
 *
 * @author Arjen Poutsma
 */
@Service
class ContentRepositoryImpl implements ContentRepository {

	private static final Logger logger = LoggerFactory.getLogger(ContentRepositoryImpl.class);

	private final Path storageDirectory;

	ContentRepositoryImpl(MtomServerProperties mtomServerProperties) {
		this.storageDirectory = mtomServerProperties.getStorageDirectory().toAbsolutePath().normalize();
	}

	@Override
	public File loadContent(String name) {
		Path file = resolve(name);
		if (!Files.isRegularFile(file)) {
			throw new ContentNotFoundException(name);
		}
		return file.toFile();
	}

	@Override
	public void storeContent(String name, DataHandler content) throws IOException {
		Path file = resolve(name);
		logger.info("Storing content in file: {}", file);

		try (InputStream in = content.getInputStream()) {
			Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
		}
		logger.info("Content stored.");
	}

	/**
	 * Resolve the file for the given name, making sure it cannot point outside the
	 * storage directory.
	 */
	private Path resolve(String name) {
		if (name == null || name.isBlank()) {
			throw new InvalidContentNameException(name);
		}
		Path file = this.storageDirectory.resolve(name + ".tmp").normalize();
		if (!this.storageDirectory.equals(file.getParent())) {
			throw new InvalidContentNameException(name);
		}
		return file;
	}

}
