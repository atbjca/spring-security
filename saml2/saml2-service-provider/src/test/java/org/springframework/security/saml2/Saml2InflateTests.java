/*
 * Copyright 2002-2026 the original author or authors.
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

package org.springframework.security.saml2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class Saml2InflateTests {

	private static final int MAX_SIZE = 1024 * 1024;

	@ParameterizedTest(name = "{0}")
	@MethodSource("saml2UtilsClasses")
	public void samlInflateWhenPayloadIsAtLimitThenReturnsPayload(Class<?> saml2UtilsClass) {
		String payload = repeat('a', MAX_SIZE);
		String inflated = inflate(saml2UtilsClass, deflate(payload));
		assertThat(inflated).isEqualTo(payload);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("saml2UtilsClasses")
	public void samlInflateWhenPayloadExceedsLimitThenFails(Class<?> saml2UtilsClass) {
		assertThatExceptionOfType(Saml2Exception.class)
			.isThrownBy(() -> inflate(saml2UtilsClass, deflate(repeat('a', MAX_SIZE + 1))))
			.withCauseInstanceOf(IOException.class)
			.withStackTraceContaining("SAML payload exceeded maximum size of " + MAX_SIZE);
	}

	static Stream<Class<?>> saml2UtilsClasses() throws ClassNotFoundException {
		return Stream.of(Class.forName("org.springframework.security.saml2.provider.service.authentication.Saml2Utils"),
				Class.forName("org.springframework.security.saml2.provider.service.authentication.logout.Saml2Utils"),
				Class.forName("org.springframework.security.saml2.provider.service.web.authentication.Saml2Utils"),
				Class.forName(
						"org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2Utils"));
	}

	private static String inflate(Class<?> saml2UtilsClass, byte[] deflated) {
		try {
			Method inflate = saml2UtilsClass.getDeclaredMethod("samlInflate", byte[].class);
			inflate.setAccessible(true);
			return (String) inflate.invoke(null, deflated);
		}
		catch (InvocationTargetException ex) {
			if (ex.getCause() instanceof RuntimeException) {
				throw (RuntimeException) ex.getCause();
			}
			throw new AssertionError(ex.getCause());
		}
		catch (ReflectiveOperationException ex) {
			throw new AssertionError(ex);
		}
	}

	private static byte[] deflate(String payload) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DeflaterOutputStream deflater = new DeflaterOutputStream(out, new Deflater(Deflater.DEFLATED, true));
			deflater.write(payload.getBytes(StandardCharsets.UTF_8));
			deflater.finish();
			return out.toByteArray();
		}
		catch (IOException ex) {
			throw new AssertionError(ex);
		}
	}

	private static String repeat(char value, int size) {
		char[] payload = new char[size];
		Arrays.fill(payload, value);
		return new String(payload);
	}

}
