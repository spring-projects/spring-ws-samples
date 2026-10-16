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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * Generate JAX-WS client classes from a WSDL, and compile them with the project.
 */
public class WsImportPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply(ConventionsPlugin.class);
		WsImportExtension extension = project.getExtensions().create("wsimport", WsImportExtension.class);
		Configuration classpath = project.getConfigurations().create("wsimport", (configuration) -> {
			configuration.setCanBeConsumed(false);
			configuration.setCanBeResolved(true);
		});
		project.getDependencies().add(classpath.getName(), "com.sun.xml.ws:jaxws-tools");
		TaskProvider<WsImportTask> wsimport = project.getTasks()
			.register("wsimport", WsImportTask.class, (task) -> {
				task.setGroup("build");
				task.setDescription("Generates JAX-WS client classes.");
				task.getSource().set(extension.getSource());
				task.getReferencedFiles().from(extension.getReferencedFiles());
				task.getPackageName().set(extension.getPackageName());
				task.getToolClasspath().from(classpath);
				task.getOutputDirectory().set(project.getLayout().getBuildDirectory().dir("generated/sources/wsimport"));
			});
		project.getExtensions()
			.getByType(SourceSetContainer.class)
			.getByName("main")
			.getJava()
			.srcDir(wsimport.flatMap(WsImportTask::getOutputDirectory));
	}

}
