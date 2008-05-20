package com.atlassian.sal.api.message;

import java.io.Serializable;

public class DefaultMessage implements Message
{
	private final Serializable[] arguments;
	private String key;

	public DefaultMessage(String key, Serializable... arguments)
	{
		this.key = key;
		this.arguments = arguments;
	}

	public Serializable[] getArguments()
	{
		return arguments;
	}

	public String getKey()
	{
		return key;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(key);
		builder.append(": ");
		for (Serializable argument : arguments)
		{
			builder.append(argument);
			builder.append(",");
		}
		return builder.toString();
	}
}

