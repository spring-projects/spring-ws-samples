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

import java.nio.file.Path;
import java.util.Map;

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import io.spring.javaformat.gradle.tasks.FormatterTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;

/**
 * Conventions that apply to every sample: Java, Spring Boot, Spring Java Format and
 * dependency management.
 */
public class ConventionsPlugin implements Plugin<Project> {

	/**
	 * The dependencies that Spring Boot does not manage, and the property of
	 * {@code gradle.properties} that holds their version.
	 */
	private static final Map<String, String> MANAGED_DEPENDENCIES = Map.ofEntries(
			Map.entry("com.sun.xml.ws:jaxws-tools", "jaxwsToolsVersion"),
			Map.entry("jakarta.jws:jakarta.jws-api", "jwsApiVersion"),
			Map.entry("org.apache.axis2:axis2-adb", "axisVersion"),
			Map.entry("org.apache.axis2:axis2-transport-local", "axisVersion"),
			Map.entry("org.apache.axis2:axis2-transport-http", "axisVersion"),
			Map.entry("org.apache.ws.xmlschema:xmlschema-core", "xmlschemaVersion"));

	@Override
	public void apply(Project project) {
		PluginManager plugins = project.getPluginManager();
		plugins.apply(JavaPlugin.class);
		plugins.apply("org.springframework.boot");
		plugins.apply("io.spring.dependency-management");
		plugins.apply("io.spring.javaformat");
		configureProject(project);
		configureJava(project);
		configureFormat(project);
		configureDependencyManagement(project);
	}

	private void configureProject(Project project) {
		project.getExtensions()
			.getByType(BasePluginExtension.class)
			.getArchivesName()
			.set(project.getPath().substring(1).replace(":", "-"));
	}

	private void configureJava(Project project) {
		project.getTasks().withType(JavaCompile.class).configureEach((compile) -> {
			compile.getOptions().getRelease().set(17);
			compile.getOptions().setEncoding("UTF-8");
			compile.getOptions().getCompilerArgs().add("-parameters");
		});
		project.getTasks().withType(Test.class).configureEach(Test::useJUnitPlatform);
		project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME,
				"org.junit.platform:junit-platform-launcher");
	}

	/**
	 * Only the sources of the project are subject to formatting, not the generated ones.
	 */
	private void configureFormat(Project project) {
		Path buildDirectory = project.getLayout().getBuildDirectory().get().getAsFile().toPath();
		project.getTasks()
			.withType(FormatterTask.class)
			.configureEach((task) -> task.exclude((element) -> element.getFile().toPath().startsWith(buildDirectory)));
	}

	/**
	 * Manage the dependencies with the semantics of Maven (a managed version always wins):
	 * the Spring Boot BOM, which the Spring Boot plugin imports, and the dependencies above.
	 */
	private void configureDependencyManagement(Project project) {
		project.getExtensions()
			.getByType(DependencyManagementExtension.class)
			.dependencies((dependencies) -> MANAGED_DEPENDENCIES.forEach((coordinates,
					property) -> dependencies.dependency(coordinates + ":" + project.property(property))));
	}

}
