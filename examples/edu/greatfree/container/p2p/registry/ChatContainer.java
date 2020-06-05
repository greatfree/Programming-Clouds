package edu.greatfree.container.p2p.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.UtilConfig;

// Created: 06/05/2020, Bing Li
class ChatContainer
{
	private Map<String, String> messages;
	
	private ChatContainer()
	{
		this.messages = new ConcurrentHashMap<String, String>();
	}
	
	private static ChatContainer instance = new ChatContainer();
	
	public static ChatContainer POLL()
	{
		if (instance == null)
		{
			instance = new ChatContainer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public boolean isEmpty(String userKey)
	{
		if (this.messages.containsKey(userKey))
		{
			return this.messages.get(userKey).equals(UtilConfig.EMPTY_STRING);
		}
		return false;
	}
	
	public String getMessage(String userKey)
	{
		if (this.messages.containsKey(userKey))
		{
			return this.messages.get(userKey);
		}
		return UtilConfig.EMPTY_STRING;
	}
	
	public void keepMessage(String userKey, String msg)
	{
		this.messages.put(userKey, msg);
	}
	
	public synchronized void clear(String userKey)
	{
		this.messages.remove(userKey);
	}
	
}
