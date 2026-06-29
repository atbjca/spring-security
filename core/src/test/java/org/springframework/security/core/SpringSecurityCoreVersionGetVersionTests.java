/*
 * Copyright 2004-present the original author or authors.
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

package org.springframework.security.core;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证 {@link SpringSecurityCoreVersion#getVersion()} 与 gradle.properties 中 springSecurityVersion 一致。
 *
 * @author BJCA fork maintenance
 */
class SpringSecurityCoreVersionGetVersionTests {

	@Test
	void getVersionShouldMatchSpringSecurityVersionInGradleProperties() throws IOException {
		Properties gradleProperties = loadGradleProperties();
		String springSecurityVersion = gradleProperties.getProperty("springSecurityVersion");
		String version = gradleProperties.getProperty("version");
		assertThat(springSecurityVersion).isNotBlank();
		assertThat(SpringSecurityCoreVersion.getVersion()).isEqualTo(springSecurityVersion);
		// 发布坐标 version 应包含上游基线版本前缀（如 6.5.11-nes.patch.1-SNAPSHOT）
		assertThat(version).startsWith(SpringSecurityCoreVersion.getVersion());
	}

	private static Properties loadGradleProperties() throws IOException {
		return PropertiesLoaderUtils.loadProperties(new FileSystemResource(findGradleProperties()));
	}

	private static File findGradleProperties() {
		File current = new File(".").getAbsoluteFile();
		while (current != null) {
			File gradleProperties = new File(current, "gradle.properties");
			if (gradleProperties.isFile()) {
				return gradleProperties;
			}
			current = current.getParentFile();
		}
		throw new IllegalStateException("Could not find gradle.properties");
	}

}
