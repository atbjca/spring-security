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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

/**
 * Extracts a CN or emailAddress principal using structured X.500 RDN parsing.
 *
 * @author Max Batischev
 * @author Rob Winch
 * @since 5.8.17
 */
public final class SubjectX500PrincipalExtractor implements X509PrincipalExtractor, MessageSourceAware {

	private static final String EMAIL_SUBJECT_DN_TYPE = "OID.1.2.840.113549.1.9.1";

	private static final String CN_SUBJECT_DN_TYPE = "CN";

	private final Log logger = LogFactory.getLog(getClass());

	private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	private String subjectDnType = CN_SUBJECT_DN_TYPE;

	private String x500PrincipalFormat = X500Principal.RFC2253;

	@Override
	public Object extractPrincipal(X509Certificate clientCert) {
		Assert.notNull(clientCert, "clientCert cannot be null");
		String subjectDn;
		try {
			X500Principal principal = clientCert.getSubjectX500Principal();
			subjectDn = principal.getName(this.x500PrincipalFormat);
		}
		catch (IllegalArgumentException ex) {
			throw new BadCredentialsException("Failed to parse client certificate", ex);
		}
		this.logger.debug(LogMessage.format("Subject DN is '%s'", subjectDn));
		String principalName = getSubject(subjectDn);
		this.logger.debug(LogMessage.format("Extracted Principal name is '%s'", principalName));
		return principalName;
	}

	private String getSubject(String subjectDn) {
		for (Rdn rdn : getRdns(subjectDn)) {
			if (this.subjectDnType.equals(rdn.getType())) {
				return String.valueOf(rdn.getValue());
			}
		}
		throw new BadCredentialsException(this.messages.getMessage("SubjectX500PrincipalExtractor.noMatching",
				new Object[] { subjectDn }, "No matching pattern was found in subject DN: {0}"));
	}

	private List<Rdn> getRdns(String subjectDn) {
		try {
			List<Rdn> rdns = new ArrayList<>(new LdapName(subjectDn).getRdns());
			// LdapName returns least-specific first.
			Collections.reverse(rdns);
			return rdns;
		}
		catch (InvalidNameException ex) {
			throw new BadCredentialsException("Failed to parse client certificate", ex);
		}
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		Assert.notNull(messageSource, "messageSource cannot be null");
		this.messages = new MessageSourceAccessor(messageSource);
	}

	/**
	 * Sets whether the principal is extracted from emailAddress instead of CN.
	 * @param extractPrincipalNameFromEmail whether to extract emailAddress
	 */
	public void setExtractPrincipalNameFromEmail(boolean extractPrincipalNameFromEmail) {
		if (extractPrincipalNameFromEmail) {
			this.subjectDnType = EMAIL_SUBJECT_DN_TYPE;
			this.x500PrincipalFormat = X500Principal.RFC1779;
		}
		else {
			this.subjectDnType = CN_SUBJECT_DN_TYPE;
			this.x500PrincipalFormat = X500Principal.RFC2253;
		}
	}

}
