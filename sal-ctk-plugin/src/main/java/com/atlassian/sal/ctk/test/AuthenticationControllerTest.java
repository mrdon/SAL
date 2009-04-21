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
public class AuthenticationControllerTest implements CtkTest
{
	private final AuthenticationController controller;

    public AuthenticationControllerTest(AuthenticationController controller)
    {
        this.controller = controller;
    }


    public void execute(final CtkTestResults results)
	{
		results.assertTrue("AuthenticationController should be injectable", controller != null);
	}
}