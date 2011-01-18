package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import junit.framework.TestCase;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public class TestAbstractI18nResolver extends TestCase
{
    private I18nResolver assertingResolver = new AbstractI18nResolver() {
        @Override
        public String resolveText(String key, Serializable[] arguments)
        {
            assertEquals(0, arguments.length);
            return "";
        }

        public Map<String, String> getAllTranslationsForPrefix(String prefix)
        {
            throw new UnsupportedOperationException();
        }

        public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale)
        {
            throw new UnsupportedOperationException();
        }
    };

    public void testGetTextWithOnlyKeyParameter()
    {
        assertingResolver.getText("hello world");
    }

    public void testGetTextWithMessageParameterWithZeroArgument()
    {
        Message message = new DefaultMessage("fun");
        assertingResolver.getText(message);
    }
}
