/*
 * Copyright 2004-2026 the original author or authors.
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

package org.springframework.security.web.authentication.preauth.x509;

import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class SubjectX500PrincipalExtractorTests {

	private final SubjectX500PrincipalExtractor extractor = new SubjectX500PrincipalExtractor();

	@Test
	public void extractWhenMultipleCnsThenReturnsMostSpecificCn() {
		X509Certificate certificate = certificate("CN=alice,OU=Engineering,CN=bob,O=Example,C=US");
		assertThat(this.extractor.extractPrincipal(certificate)).isEqualTo("alice");
	}

	@Test
	public void extractWhenOtherAttributeContainsDnTextThenReturnsActualCn() {
		X509Certificate certificate = certificate("CN=luke,OU=CN\\=duke\\,,O=Example,C=US");
		assertThat(this.extractor.extractPrincipal(certificate)).isEqualTo("luke");
	}

	@Test
	public void extractWhenOnlyOtherAttributeContainsDnTextThenBadCredentials() {
		X509Certificate certificate = certificate("OU=CN\\=duke\\,,O=Example,C=US");
		assertThatExceptionOfType(BadCredentialsException.class)
			.isThrownBy(() -> this.extractor.extractPrincipal(certificate));
	}

	@Test
	public void extractWhenEmailConfiguredThenReturnsEmailAddress() {
		this.extractor.setExtractPrincipalNameFromEmail(true);
		X509Certificate certificate = certificate("EMAILADDRESS=luke@monkeymachine,CN=luke");
		assertThat(this.extractor.extractPrincipal(certificate)).isEqualTo("luke@monkeymachine");
	}

	@Test
	public void extractWhenSubjectIsMalformedThenBadCredentials() {
		X509Certificate certificate = mock(X509Certificate.class);
		given(certificate.getSubjectX500Principal()).willThrow(new IllegalArgumentException("malformed subject"));
		assertThatExceptionOfType(BadCredentialsException.class)
			.isThrownBy(() -> this.extractor.extractPrincipal(certificate))
			.withMessage("Failed to parse client certificate")
			.withCauseInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void extractWhenCertificateIsNullThenIllegalArgument() {
		assertThatIllegalArgumentException().isThrownBy(() -> this.extractor.extractPrincipal(null));
	}

	@Test
	public void x509AuthenticationFilterWhenNoExtractorConfiguredThenUsesStructuredExtractor() {
		X509Certificate certificate = certificate("CN=alice,OU=Engineering,CN=bob,O=Example,C=US");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("javax.servlet.request.X509Certificate", new X509Certificate[] { certificate });
		X509AuthenticationFilter filter = new X509AuthenticationFilter();
		assertThat(filter.getPreAuthenticatedPrincipal(request)).isEqualTo("alice");
	}

	private X509Certificate certificate(String subjectDn) {
		X509Certificate certificate = mock(X509Certificate.class);
		given(certificate.getSubjectX500Principal()).willReturn(new X500Principal(subjectDn));
		return certificate;
	}

}
