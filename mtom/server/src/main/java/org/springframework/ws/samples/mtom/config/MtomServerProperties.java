package org.springframework.ws.samples.mtom.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties of the MTOM server.
 *
 * @author Stephane Nicoll
 */
@ConfigurationProperties("mtom")
public class MtomServerProperties {

	private Path storageDirectory = Path.of(System.getProperty("java.io.tmpdir"));

	public Path getStorageDirectory() {
		return this.storageDirectory;
	}

	public void setStorageDirectory(Path storageDirectory) {
		this.storageDirectory = storageDirectory;
	}

}
