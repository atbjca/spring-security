/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.security.convention.versions;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskProvider;

public class VerifyDependenciesVersionsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		TaskProvider<VerifyDependenciesVersionsTask> verifyDependenciesVersionsTaskProvider = project.getTasks().register("verifyDependenciesVersions", VerifyDependenciesVersionsTask.class, (task) -> {
			task.setGroup("Verification");
			task.setDescription("Verify that specific dependencies are using the same version");
			VersionCatalog versionCatalog = project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs");
			MinimalExternalModuleDependency oauth2OidcSdk = versionCatalog.findLibrary("com-nimbusds-oauth2-oidc-sdk").get().get();
			MinimalExternalModuleDependency nimbusJoseJwt = versionCatalog.findLibrary("com-nimbusds-nimbus-jose-jwt").get().get();
			task.setOauth2OidcSdkVersion(oauth2OidcSdk.getVersionConstraint().getDisplayName());
			task.setExpectedNimbusJoseJwtVersion(nimbusJoseJwt.getVersionConstraint().getDisplayName());
		});
		project.getTasks().named(JavaBasePlugin.CHECK_TASK_NAME, checkTask -> checkTask.dependsOn(verifyDependenciesVersionsTaskProvider));
	}

	/**
	 * 比较两个语义化版本号，返回负数、零或正数。
	 * 版本格式：主版本.次版本[.修订版本]
	 * 例如："10.8" vs "10.6" 返回正数（10.8 > 10.6）
	 *
	 * @param version1 第一个版本号
	 * @param version2 第二个版本号
	 * @return 负数表示 version1 < version2，零表示相等，正数表示 version1 > version2
	 */
	static int compareVersions(String version1, String version2) {
		String[] parts1 = version1.split("\\.");
		String[] parts2 = version2.split("\\.");
		int maxLength = Math.max(parts1.length, parts2.length);
		for (int i = 0; i < maxLength; i++) {
			int v1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
			int v2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
			if (v1 != v2) {
				return v1 - v2;
			}
		}
		return 0;
	}

	public static class VerifyDependenciesVersionsTask extends DefaultTask {

		private String oauth2OidcSdkVersion;

		private String expectedNimbusJoseJwtVersion;

		public void setOauth2OidcSdkVersion(String oauth2OidcSdkVersion) {
			this.oauth2OidcSdkVersion = oauth2OidcSdkVersion;
		}

		public void setExpectedNimbusJoseJwtVersion(String expectedNimbusJoseJwtVersion) {
			this.expectedNimbusJoseJwtVersion = expectedNimbusJoseJwtVersion;
		}

		@TaskAction
		public void verify() {
			String transitiveNimbusJoseJwtVersion = TransitiveDependencyLookupUtils.lookupJwtVersion(this.oauth2OidcSdkVersion);
			// 验证逻辑：允许项目声明的 nimbus-jose-jwt 版本 >= oauth2-oidc-sdk 传递依赖的版本。
			// 原因：为修复安全漏洞（CVE-2023-52428、CVE-2025-53864），需要使用比
			// oauth2-oidc-sdk 传递依赖更高的 nimbus-jose-jwt 版本。
			// 同一主版本内（如 10.6 -> 10.8）向后兼容，因此允许覆盖为更高版本。
			// 但仍需确保主版本号一致（如都是 10.x），防止跨主版本不兼容。
			String[] transitiveParts = transitiveNimbusJoseJwtVersion.split("\\.");
			String[] expectedParts = this.expectedNimbusJoseJwtVersion.split("\\.");
			boolean sameMajorVersion = transitiveParts.length > 0 && expectedParts.length > 0
					&& transitiveParts[0].equals(expectedParts[0]);
			if (!sameMajorVersion) {
				String message = String.format(
						"nimbus-jose-jwt 主版本号不一致：oauth2-oidc-sdk:%s 传递依赖 nimbus-jose-jwt:%s，"
								+ "但项目声明的版本是 nimbus-jose-jwt:%s。主版本号必须一致。",
						this.oauth2OidcSdkVersion, transitiveNimbusJoseJwtVersion,
						this.expectedNimbusJoseJwtVersion);
				throw new IllegalStateException(message);
			}
			if (compareVersions(this.expectedNimbusJoseJwtVersion, transitiveNimbusJoseJwtVersion) < 0) {
				String message = String.format(
						"项目声明的 nimbus-jose-jwt:%s 低于 oauth2-oidc-sdk:%s 传递依赖的版本 nimbus-jose-jwt:%s。"
								+ "请将 nimbus-jose-jwt 版本升级到至少 %s。",
						this.expectedNimbusJoseJwtVersion, this.oauth2OidcSdkVersion,
						transitiveNimbusJoseJwtVersion, transitiveNimbusJoseJwtVersion);
				throw new IllegalStateException(message);
			}
			// 项目声明的版本 >= 传递依赖版本，且主版本号一致，验证通过
			if (!transitiveNimbusJoseJwtVersion.equals(this.expectedNimbusJoseJwtVersion)) {
				getLogger().warn(
						"注意：项目声明的 nimbus-jose-jwt:{} 高于 oauth2-oidc-sdk:{} 传递依赖的版本 nimbus-jose-jwt:{}。"
								+ "这是为了修复安全漏洞而有意为之。",
						this.expectedNimbusJoseJwtVersion, this.oauth2OidcSdkVersion,
						transitiveNimbusJoseJwtVersion);
			}
		}

	}

}
