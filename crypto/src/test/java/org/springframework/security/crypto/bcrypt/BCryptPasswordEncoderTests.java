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

	// === CVE-2025-22228 修复验证测试 ===

	/**
	 * 测试 72 字节密码可以正常编码和验证 72 字节是 BCrypt 算法的最大有效密码长度
	 */
	@Test
	public void encodeWhenPasswordIs72BytesThenSuccess() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 72 个 ASCII 字符 = 72 字节
		String password72Bytes = repeatChar('A', 72);
		String encoded = encoder.encode(password72Bytes);
		assertThat(encoder.matches(password72Bytes, encoded)).isTrue();
	}

	/**
	 * 测试超过 72 字节的密码在编码时被拒绝（CVE-2025-22228 修复）
	 */
	@Test
	public void encodeWhenPasswordExceeds72BytesThenThrowsException() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 73 个 ASCII 字符 = 73 字节
		String password73Bytes = repeatChar('A', 73);
		assertThatIllegalArgumentException().isThrownBy(() -> encoder.encode(password73Bytes))
			.withMessageContaining("72 bytes");
	}

	/**
	 * 测试超过 72 字节的密码在验证时不抛出异常（CVE-2025-22234 回归修复） matches() 路径必须正常执行，以保持
	 * DaoAuthenticationProvider 的时序攻击防护
	 */
	@Test
	public void matchesWhenPasswordExceeds72BytesThenDoesNotThrowException() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 先编码一个短密码
		String shortPassword = "password";
		String encoded = encoder.encode(shortPassword);
		// 用超长密码去 matches，不应抛出异常
		String longPassword = repeatChar('A', 100);
		assertThat(encoder.matches(longPassword, encoded)).isFalse();
	}

	/**
	 * 测试多字节字符密码的字节长度边界 中文字符在 UTF-8 编码下占 3 字节，24 个中文字符 = 72 字节
	 */
	@Test
	public void encodeWhenMultiBytePasswordExceeds72BytesThenThrowsException() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 25 个中文字符 = 75 字节（UTF-8），超过 72 字节限制
		String multiBytePassword = "密码测试用例一二三四五六七八九零壹贰叁肆伍陆柒捌玖";
		assertThatIllegalArgumentException().isThrownBy(() -> encoder.encode(multiBytePassword));
	}

	/**
	 * 测试前 72 字节相同的超长密码在 matches 中仍然可以匹配 这验证了 matches 路径的 for_check 行为
	 */
	@Test
	public void matchesWhenLongPasswordSharesFirst72BytesThenMatches() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 编码一个刚好 72 字节的密码
		String password72 = repeatChar('A', 72);
		String encoded = encoder.encode(password72);
		// 超长密码（前 72 字节与 password72 相同），matches 不抛异常且返回 true
		String longPassword = repeatChar('A', 100);
		assertThat(encoder.matches(longPassword, encoded)).isTrue();
	}

	/**
	 * 辅助方法：生成重复字符的字符串（兼容 Java 8，替代 String.repeat()）
	 */
	private static String repeatChar(char c, int count) {
		char[] chars = new char[count];
		java.util.Arrays.fill(chars, c);
		return new String(chars);
	}

}
