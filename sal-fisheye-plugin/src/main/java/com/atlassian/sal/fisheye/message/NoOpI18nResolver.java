package com.atlassian.sal.fisheye.message;

import java.io.Serializable;
import java.util.Arrays;

import com.atlassian.sal.api.message.AbstractI18nResolver;

public class NoOpI18nResolver extends AbstractI18nResolver
{
	@Override
	public String resolveText(String key, Serializable[] arguments)
	{
		return "["+key+":"+Arrays.asList(arguments)+"]";
	}

}
