/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.security.crypto.bcrypt;

import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Dave Syer
 *
 */
public class BCryptPasswordEncoderTests {

	@Test
	// gh-5548
	public void emptyRawPasswordDoesNotMatchPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String result = encoder.encode("password");
		assertThat(encoder.matches("", result)).isFalse();
	}

	@Test
	public void $2yMatches() {
		// $2y is default version
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String result = encoder.encode("password");
		assertThat(result.equals("password")).isFalse();
		assertThat(encoder.matches("password", result)).isTrue();
	}

	@Test
	public void $2aMatches() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A);
		String result = encoder.encode("password");
		assertThat(result.equals("password")).isFalse();
		assertThat(encoder.matches("password", result)).isTrue();
	}

	@Test
	public void $2bMatches() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
		String result = encoder.encode("password");
		assertThat(result.equals("password")).isFalse();
		assertThat(encoder.matches("password", result)).isTrue();
	}

	@Test
	public void $2yUnicode() {
		// $2y is default version
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String result = encoder.encode("passw\u9292rd");
		assertThat(encoder.matches("pass\u9292\u9292rd", result)).isFalse();
		assertThat(encoder.matches("passw\u9292rd", result)).isTrue();
	}

	@Test
	public void $2aUnicode() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A);
		String result = encoder.encode("passw\u9292rd");
		assertThat(encoder.matches("pass\u9292\u9292rd", result)).isFalse();
		assertThat(encoder.matches("passw\u9292rd", result)).isTrue();
	}

	@Test
	public void $2bUnicode() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
		String result = encoder.encode("passw\u9292rd");
		assertThat(encoder.matches("pass\u9292\u9292rd", result)).isFalse();
		assertThat(encoder.matches("passw\u9292rd", result)).isTrue();
	}

	@Test
	public void $2yNotMatches() {
		// $2y is default version
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String result = encoder.encode("password");
		assertThat(encoder.matches("bogus", result)).isFalse();
	}

	@Test
	public void $2aNotMatches() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A);
		String result = encoder.encode("password");
		assertThat(encoder.matches("bogus", result)).isFalse();
	}

	@Test
	public void $2bNotMatches() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
		String result = encoder.encode("password");
		assertThat(encoder.matches("bogus", result)).isFalse();
	}

	@Test
	public void $2yCustomStrength() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);
		String result = encoder.encode("password");
		assertThat(encoder.matches("password", result)).isTrue();
	}

	@Test
	public void $2aCustomStrength() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 8);
		String result = encoder.encode("password");
		assertThat(encoder.matches("password", result)).isTrue();
	}

	@Test
	public void $2bCustomStrength() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 8);
		String result = encoder.encode("password");
		assertThat(encoder.matches("password", result)).isTrue();
	}

	@Test
	public void badLowCustomStrength() {
		assertThatIllegalArgumentException().isThrownBy(() -> new BCryptPasswordEncoder(3));
	}

	@Test
	public void badHighCustomStrength() {
		assertThatIllegalArgumentException().isThrownBy(() -> new BCryptPasswordEncoder(32));
	}

	@Test
	public void customRandom() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8, new SecureRandom());
		String result = encoder.encode("password");
		assertThat(encoder.matches("password", result)).isTrue();
	}

	@Test
	public void doesntMatchNullEncodedValue() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThat(encoder.matches("password", null)).isFalse();
	}

	@Test
	public void doesntMatchEmptyEncodedValue() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThat(encoder.matches("password", "")).isFalse();
	}

	@Test
	public void doesntMatchBogusEncodedValue() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThat(encoder.matches("password", "012345678901234567890123456789")).isFalse();
	}

	@Test
	public void upgradeFromLowerStrength() {
		BCryptPasswordEncoder weakEncoder = new BCryptPasswordEncoder(5);
		BCryptPasswordEncoder strongEncoder = new BCryptPasswordEncoder(15);
		String weakPassword = weakEncoder.encode("password");
		String strongPassword = strongEncoder.encode("password");
		assertThat(weakEncoder.upgradeEncoding(strongPassword)).isFalse();
		assertThat(strongEncoder.upgradeEncoding(weakPassword)).isTrue();
	}

	/**
	 * @see <a href=
	 * "https://github.com/spring-projects/spring-security/pull/7042#issuecomment-506755496">https://github.com/spring-projects/spring-security/pull/7042#issuecomment-506755496</a>
	 */
	@Test
	public void upgradeFromNullOrEmpty() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThat(encoder.upgradeEncoding(null)).isFalse();
		assertThat(encoder.upgradeEncoding("")).isFalse();
	}

	/**
	 * @see <a href=
	 * "https://github.com/spring-projects/spring-security/pull/7042#issuecomment-506755496">https://github.com/spring-projects/spring-security/pull/7042#issuecomment-506755496</a>
	 */
	@Test
	public void upgradeFromNonBCrypt() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThatIllegalArgumentException().isThrownBy(() -> encoder.upgradeEncoding("not-a-bcrypt-password"));
	}

	@Test
	public void encodeNullRawPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThatIllegalArgumentException().isThrownBy(() -> encoder.encode(null));
	}

	@Test
	public void matchNullRawPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThatIllegalArgumentException().isThrownBy(() -> encoder.matches(null, "does-not-matter"));
	}

	@Test
	public void upgradeWhenNoRoundsThenTrue() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThat(encoder.upgradeEncoding("$2a$00$9N8N35BVs5TLqGL3pspAte5OWWA2a2aZIs.EGp7At7txYakFERMue")).isTrue();
	}

	@Test
	public void checkWhenNoRoundsThenTrue() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertThat(encoder.matches("password", "$2a$00$9N8N35BVs5TLqGL3pspAte5OWWA2a2aZIs.EGp7At7txYakFERMue"))
				.isTrue();
		assertThat(encoder.matches("wrong", "$2a$00$9N8N35BVs5TLqGL3pspAte5OWWA2a2aZIs.EGp7At7txYakFERMue")).isFalse();
	}

	// ===== CVE-2025-22228 & CVE-2025-22234 修复验证测试 =====

	@Test
	// 正常长度密码（< 72 字节）的编码和验证功能不受修复影响
	public void encodeAndMatchNormalLengthPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String normalPassword = "mySecurePassword123!";
		String encoded = encoder.encode(normalPassword);
		assertThat(encoder.matches(normalPassword, encoded)).isTrue();
		assertThat(encoder.matches("wrongPassword", encoded)).isFalse();
	}

	@Test
	// CVE-2025-22228 修复验证（边界值）：恰好 72 字节的密码应正常编码和验证
	public void encodeWhenPasswordAtMaxLengthThenSuccess() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 构造恰好 72 字节的 ASCII 密码
		char[] chars72 = new char[72];
		Arrays.fill(chars72, 'a');
		String password72 = new String(chars72);
		// 编码应成功
		String encoded = encoder.encode(password72);
		// 验证应匹配
		assertThat(encoder.matches(password72, encoded)).isTrue();
	}

	@Test
	// CVE-2025-22228 修复验证：超过 72 字节的新密码编码时应抛出 IllegalArgumentException
	public void encodeWhenPasswordOverMaxLengthThenThrowIllegalArgumentException() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 构造 73 字节的 ASCII 密码
		char[] chars73 = new char[73];
		Arrays.fill(chars73, 'a');
		String password73 = new String(chars73);
		// 编码超长密码应抛出异常
		assertThatIllegalArgumentException().isThrownBy(() -> encoder.encode(password73));
	}

	@Test
	// CVE-2025-22234 修复验证：超过 72 字节的已有密码验证时不应抛异常，应正常匹配
	// 这确保了向后兼容性——修复前已存储的长密码哈希仍然可以正常验证
	public void matchesWhenPasswordOverMaxLengthThenAllowToMatch() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 先用 72 字节密码生成哈希（模拟修复前存储的长密码哈希）
		char[] chars72 = new char[72];
		Arrays.fill(chars72, 'a');
		String password72 = new String(chars72);
		String encoded = encoder.encode(password72);
		// 用 73 字节密码验证——不应抛异常，且应匹配
		// （因为 BCrypt 内部截断行为，前 72 字节相同即匹配）
		char[] chars73 = new char[73];
		Arrays.fill(chars73, 'a');
		String password73 = new String(chars73);
		assertThat(encoder.matches(password73, encoded)).isTrue();
	}

	@Test
	// CVE-2025-22228 修复验证（多字节 UTF-8 字符）：
	// 密码长度校验基于 UTF-8 字节数而非字符数。
	// 中文字符 '中' 在 UTF-8 中占 3 字节，24 个 '中' = 72 字节应成功，25 个 = 75 字节应失败
	public void encodeWhenMultiBytePasswordOverMaxByteLengthThenThrowIllegalArgumentException() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 24 个中文字符 = 72 UTF-8 字节（边界值，应成功编码）
		char[] chars24 = new char[24];
		Arrays.fill(chars24, '\u4e2d');
		String password24Chinese = new String(chars24);
		String encoded = encoder.encode(password24Chinese);
		assertThat(encoder.matches(password24Chinese, encoded)).isTrue();
		// 25 个中文字符 = 75 UTF-8 字节（超过 72 字节限制，应抛异常）
		char[] chars25 = new char[25];
		Arrays.fill(chars25, '\u4e2d');
		String password25Chinese = new String(chars25);
		assertThatIllegalArgumentException().isThrownBy(() -> encoder.encode(password25Chinese));
	}

}
