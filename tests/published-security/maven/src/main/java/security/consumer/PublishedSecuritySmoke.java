package security.consumer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.springframework.security.crypto.encrypt.BouncyCastleAesGcmBytesEncryptor;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.saml2.core.OpenSamlInitializationService;

public final class PublishedSecuritySmoke {

	private PublishedSecuritySmoke() {
	}

	public static void main(String[] args) throws Exception {
		OpenSamlInitializationService.initialize();
		assertMissing("org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider");

		byte[] clear = "java8-security-smoke".getBytes(StandardCharsets.UTF_8);
		BouncyCastleAesGcmBytesEncryptor encryptor = new BouncyCastleAesGcmBytesEncryptor("password",
				"5c0744940b5c369b");
		byte[] encrypted = encryptor.encrypt(clear);
		if (!Arrays.equals(clear, encryptor.decrypt(encrypted))) {
			throw new IllegalStateException("Bouncy Castle encryption round trip failed");
		}

		Class.forName(BindAuthenticator.class.getName());
		Class.forName("org.springframework.security.openid.OpenID4JavaConsumer");
		Class.forName("org.apache.xerces.parsers.DOMParser");
		System.out.println("published-security-smoke: OK");
	}

	private static void assertMissing(String className) throws Exception {
		try {
			Class.forName(className);
		}
		catch (ClassNotFoundException expected) {
			return;
		}
		throw new IllegalStateException(className + " must not be present in the Java 8 publication");
	}

}
