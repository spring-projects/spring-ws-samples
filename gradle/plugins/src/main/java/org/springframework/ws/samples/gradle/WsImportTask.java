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

import java.io.File;
import java.util.List;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * Generate JAX-WS client sources from a WSDL using {@code wsimport}.
 */
@CacheableTask
public abstract class WsImportTask extends AbstractGeneratorTask {

	@Input
	public abstract Property<String> getPackageName();

	@TaskAction
	void generate() {
		File classes = new File(getTemporaryDir(), "classes");
		classes.mkdirs();
		run("com.sun.tools.ws.WsImport",
				List.of("-keep", "-Xnocompile", "-extension", "-quiet", "-s",
						getOutputDirectory().get().getAsFile().getAbsolutePath(), "-d", classes.getAbsolutePath(), "-p",
						getPackageName().get(), getSource().get().getAsFile().getAbsolutePath()));
	}

}
