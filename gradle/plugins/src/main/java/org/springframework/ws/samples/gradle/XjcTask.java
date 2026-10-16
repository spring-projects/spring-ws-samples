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

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * Generate JAXB classes from a schema, or from the schema that a WSDL defines, using
 * XJC.
 */
@CacheableTask
public abstract class XjcTask extends AbstractGeneratorTask {

	@Input
	public abstract Property<String> getPackageName();

	/**
	 * Whether the source is a WSDL rather than a schema.
	 */
	@Input
	public abstract Property<Boolean> getWsdl();

	@TaskAction
	void generate() {
		List<String> args = new ArrayList<>(List.of("-d", getOutputDirectory().get().getAsFile().getAbsolutePath(),
				"-p", getPackageName().get(), "-quiet"));
		if (getWsdl().get()) {
			args.add("-wsdl");
		}
		args.add(getSource().get().getAsFile().getAbsolutePath());
		run("com.sun.tools.xjc.XJCFacade", args);
	}

}
