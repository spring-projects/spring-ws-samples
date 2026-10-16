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

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemOperations;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.process.ExecOperations;

/**
 * Base class for the tasks that generate sources from a schema or a WSDL by running a
 * command line tool.
 */
@CacheableTask
public abstract class AbstractGeneratorTask extends DefaultTask {

	/**
	 * The schema or WSDL to generate the sources from.
	 */
	@InputFile
	@PathSensitive(PathSensitivity.NONE)
	public abstract RegularFileProperty getSource();

	/**
	 * Files that the source refers to, such as imported schemas.
	 */
	@InputFiles
	@PathSensitive(PathSensitivity.NONE)
	public abstract ConfigurableFileCollection getReferencedFiles();

	/**
	 * The classpath of the tool that generates the sources.
	 */
	@Classpath
	public abstract ConfigurableFileCollection getToolClasspath();

	@OutputDirectory
	public abstract DirectoryProperty getOutputDirectory();

	@Inject
	protected abstract ExecOperations getExecOperations();

	@Inject
	protected abstract FileSystemOperations getFileSystemOperations();

	protected final void run(String mainClass, List<String> args) {
		File output = getOutputDirectory().get().getAsFile();
		getFileSystemOperations().delete((delete) -> delete.delete(output));
		output.mkdirs();
		getExecOperations().javaexec((exec) -> {
			exec.classpath(getToolClasspath());
			exec.getMainClass().set(mainClass);
			exec.args(args);
		});
	}

}
