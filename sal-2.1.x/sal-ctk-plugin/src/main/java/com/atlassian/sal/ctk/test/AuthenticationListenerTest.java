package com.atlassian.sal.ctk.test;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class AuthenticationListenerTest implements CtkTest
{
	private final AuthenticationListener authenticationListener;

    public AuthenticationListenerTest(AuthenticationListener authenticationListener)
    {
        this.authenticationListener = authenticationListener;
    }


    public void execute(final CtkTestResults results)
	{
		results.assertTrue("AuthenticationListener should be injectable", authenticationListener != null);
	}
}