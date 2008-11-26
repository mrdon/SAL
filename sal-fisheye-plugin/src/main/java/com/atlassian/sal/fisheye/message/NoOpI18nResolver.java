package com.atlassian.sal.fisheye.message;

import java.io.Serializable;

import com.atlassian.sal.core.message.AbstractI18nResolver;


public class NoOpI18nResolver extends AbstractI18nResolver
{
	@Override
	public String resolveText(String key, Serializable[] arguments)
	{
		return key;
	}

}
