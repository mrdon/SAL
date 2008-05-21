package com.atlassian.sal.api.message;

import java.io.Serializable;

public interface Message extends Serializable
{
	String getKey();

	Serializable[] getArguments();
}
