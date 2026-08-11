/*
 * Copyright 2016-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.spring.gradle.convention;

import java.util.zip.ZipFile

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.plugins.PluginManager
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.bundling.Jar
import org.springframework.gradle.classpath.CheckClasspathForProhibitedDependenciesPlugin;
import org.springframework.gradle.maven.SpringMavenPlugin;

/**
 * @author Rob Winch
 */
class SpringModulePlugin extends AbstractSpringJavaPlugin {

	@Override
	void additionalPlugins(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(JavaLibraryPlugin.class)
		pluginManager.apply(SpringMavenPlugin.class);
		pluginManager.apply(CheckClasspathForProhibitedDependenciesPlugin.class);
		pluginManager.apply("io.spring.convention.jacoco");

		def deployArtifacts = project.task("deployArtifacts")
		deployArtifacts.group = 'Deploy tasks'
		deployArtifacts.description = "Deploys the artifacts to either Artifactory or Maven Central"
		if (!Utils.isRelease(project)) {
			deployArtifacts.dependsOn project.tasks.artifactoryPublish
		}

		def moduleJar = project.tasks.named('jar', Jar)
		def checkJava8Artifact = project.tasks.register('checkJava8Artifact') {
			group = 'verification'
			description = 'Checks that the published main JAR is loadable by Java 8'
			dependsOn(moduleJar)
			inputs.file(moduleJar.flatMap { it.archiveFile })
			doLast {
				File artifact = moduleJar.get().archiveFile.get().asFile
				List<String> violations = []
				new ZipFile(artifact).withCloseable { zip ->
					zip.entries().findAll { !it.directory && it.name.endsWith('.class') }.each { entry ->
						zip.getInputStream(entry).withCloseable { input ->
							byte[] header = new byte[8]
							int read = input.read(header)
							if (read != header.length) {
								violations << "${entry.name}: truncated class file header"
							}
							else {
								int major = ((header[6] & 0xff) << 8) | (header[7] & 0xff)
								if (major > 52) {
									violations << "${entry.name}: class-file major ${major} exceeds Java 8 major 52"
								}
							}
						}
					}
				}
				if (!violations.isEmpty()) {
					throw new GradleException("Java 8 artifact verification failed for ${artifact}:\n"
							+ violations.collect { " - ${it}" }.join('\n'))
				}
			}
		}

		project.tasks.named('check') {
			dependsOn(checkJava8Artifact)
		}
		project.tasks.withType(AbstractPublishToMaven).configureEach {
			dependsOn(checkJava8Artifact)
		}
		project.tasks.matching { it.name == 'artifactoryPublish' }.configureEach {
			dependsOn(checkJava8Artifact)
		}
	}

}
