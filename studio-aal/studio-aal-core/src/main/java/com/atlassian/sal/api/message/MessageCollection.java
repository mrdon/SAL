package com.atlassian.sal.api.message;

import java.io.Serializable;
import java.util.List;

public interface MessageCollection
{
	void addMessage(String key, Serializable... arguments);

	void addMessage(Message message);

	void addAll(List<Message> remoteMessages);

	boolean isEmpty();

	List<Message> getMessages();

}
