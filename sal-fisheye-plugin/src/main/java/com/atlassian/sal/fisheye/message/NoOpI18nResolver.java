package com.atlassian.sal.fisheye.message;

import com.atlassian.sal.core.message.AbstractI18nResolver;

import java.io.Serializable;
import java.util.Arrays;


public class NoOpI18nResolver extends AbstractI18nResolver
{
	@Override
	public String resolveText(String key, Serializable[] arguments)
	{
        return null;
	}

}
