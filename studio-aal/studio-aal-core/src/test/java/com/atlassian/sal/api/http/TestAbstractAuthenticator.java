package com.atlassian.sal.api.http;

import com.atlassian.sal.api.http.Authenticator;

import junit.framework.TestCase;


public class TestAbstractAuthenticator extends TestCase
{

    public void testUserNameStored()
    {
        AuthenticatorTester tester = new AuthenticatorTester();
        final String testUserName = "testUserName";

        tester.setProperty("username", testUserName);
        assertEquals(testUserName, tester.getProperty("username"));
    }

    public void testPasswordStored()
    {
        AuthenticatorTester tester = new AuthenticatorTester();
        final String testUserName = "testPassword";

        tester.setProperty("password", testUserName);
        assertEquals(testUserName, tester.getProperty("password"));
    }

    public void testInvalidString()
    {
        String invalidString = "com.atlassian.sal.api.http.TestAbstractAuthenticator$testInvalidString.invalidString";
        String[] validStrings = Authenticator.getPropertyNames();

        for(String string : validStrings)
        {
            TestCase.assertFalse(invalidString.equals(string));
        }

        AuthenticatorTester tester = new AuthenticatorTester();
        String testString = "testString";

        try
        {
            tester.setProperty(invalidString, testString);
            fail("Exception should be thrown for invalid keys; exception not thrown");
        }
        catch(Exception e)
        {
            // do nothing; we're tesing for an exception
        }

        try
        {
            tester.setProperty(null, testString);
            fail("Exception should be thrown for null keys; exception not thrown");
        }
        catch(Exception e)
        {
            // do nothing; we're tesing for an exception
        }

    }

    public void testBackwardsCompatibility()
    {
        String[] validStrings = Authenticator.getPropertyNames();

        assertTrue(validStrings.length >= 2);
        assertTrue("username".equals(validStrings[0]));
        assertTrue("password".equals(validStrings[1]));
    }

    private static final class AuthenticatorTester extends Authenticator
    {
        public String getProperty(String key)
        {
            // expose super.getProperty
            return super.getProperty(key);
        }
    }
}
