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

package org.springframework.security.web;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class FormPostRedirectStrategyTests {

	private FormPostRedirectStrategy redirectStrategy;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	@BeforeEach
	public void setUp() {
		this.redirectStrategy = new FormPostRedirectStrategy();
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
	}

	@Test
	public void sendRedirectWhenUrlHasEncodedParametersThenPostsDecodedValues() throws Exception {
		this.redirectStrategy.sendRedirect(this.request, this.response,
				"https://example.com/path?payload=a%2Bb%2Fc%3D&relay=https%3A%2F%2Fexample.org%2Fa%3Fx%3D1");
		assertThat(this.response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(this.response.getContentType()).isEqualTo(MediaType.TEXT_HTML_VALUE);
		assertThat(this.response.getContentAsString()).contains("action=\"https://example.com/path\"")
			.contains("<input name=\"payload\" type=\"hidden\" value=\"a+b/c=\" />")
			.contains("<input name=\"relay\" type=\"hidden\" value=\"https://example.org/a?x=1\" />");
		assertNonceMatchesPolicy();
	}

	@Test
	public void sendRedirectWhenActionNameAndValueContainHtmlThenEncodesEachAttribute() throws Exception {
		String action = "https://example.com/sso\"><script>alert(1)</script>&path";
		String name = "na\"me<&";
		String value = "va\"lue<>&";
		String url = action + "?" + UriUtils.encode(name, StandardCharsets.UTF_8) + "="
				+ UriUtils.encode(value, StandardCharsets.UTF_8);
		this.redirectStrategy.sendRedirect(this.request, this.response, url);
		String html = this.response.getContentAsString();
		assertThat(html).doesNotContain("\"><script>alert(1)</script>")
			.contains("action=\"https://example.com/sso&quot;&gt;&lt;script&gt;alert(1)&lt;/script&gt;&amp;path\"")
			.contains("name=\"na&quot;me&lt;&amp;\"")
			.contains("value=\"va&quot;lue&lt;&gt;&amp;\"");
	}

	private void assertNonceMatchesPolicy() throws Exception {
		String policy = this.response.getHeader("Content-Security-Policy");
		assertThat(policy).matches("script-src 'nonce-.+'");
		String nonce = policy.substring("script-src 'nonce-".length(), policy.length() - 1);
		assertThat(this.response.getContentAsString()).contains("<script nonce=\"" + nonce + "\">");
	}

}
