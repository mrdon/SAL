package com.atlassian.sal.ctk.test;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class I18nResolverTest implements CtkTest
{
	private final I18nResolver resolver;

	public I18nResolverTest(final I18nResolver resolver)
	{
		this.resolver = resolver;
	}

	public void execute(final CtkTestResults results)
	{
		results.assertTrue("I18nResolver should be injectable", resolver != null);

		final Message msg = resolver.createMessage("key", "arg1");

		results.assertTrue("Should create valid message", msg.getArguments().length == 1 && "key".equals(msg.getKey()));
		results.assertTrue("Should create message collection", resolver.createMessageCollection() != null);
		results.assertTrue("Should return key if text can't be resolved", "some.key.that.doesnt.exist".equals(resolver.getText("some.key.that.doesnt.exist")));
        final Map<String,String> translations = resolver.getAllTranslationsForPrefix("some.key.that.doesnt.exist", Locale.US);
        results.assertTrue("Should return empty map of translations for unknown key", translations.keySet().isEmpty());
	}
}