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

package org.springframework.ws.samples.gradle;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

/**
 * Configuration of the {@code samples.wsimport} plugin.
 */
public abstract class WsImportExtension {

	/**
	 * The WSDL to generate the client from.
	 */
	public abstract RegularFileProperty getSource();

	/**
	 * The files that the WSDL refers to, such as imported schemas.
	 */
	public abstract ConfigurableFileCollection getReferencedFiles();

	/**
	 * The package of the generated classes.
	 */
	public abstract Property<String> getPackageName();

}
