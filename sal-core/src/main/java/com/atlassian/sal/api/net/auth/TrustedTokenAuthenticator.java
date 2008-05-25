package com.atlassian.sal.api.net.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import com.atlassian.security.auth.trustedapps.request.commonshttpclient.CommonsHttpClientTrustedRequest;

public class TrustedTokenAuthenticator implements HttpClientAuthenticator
{
	private final EncryptedCertificate userCertificate;

	public TrustedTokenAuthenticator(String username)
	{
		final CertificateFactory certificateFactory = ComponentLocator.getComponent(CertificateFactory.class);
		this.userCertificate = certificateFactory.createCertificate(username);
	}

	/**
	 * @param httpClient
	 * @param method
	 */
	public void process(HttpClient httpClient, HttpMethod method)
	{
		final CommonsHttpClientTrustedRequest commonsHttpClientTrustedRequest = new CommonsHttpClientTrustedRequest(method);
		TrustedApplicationUtils.addRequestParameters(userCertificate, commonsHttpClientTrustedRequest);
	}

}