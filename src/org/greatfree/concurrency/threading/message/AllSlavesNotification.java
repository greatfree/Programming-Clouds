package org.greatfree.concurrency.threading.message;

import java.util.Map;
import java.util.Set;

import org.greatfree.message.container.Notification;

// Created: 09/20/2019, Bing Li
public class AllSlavesNotification extends Notification
{
	private static final long serialVersionUID = -3243554056020181181L;

	private String masterName;
	private Set<String> allSlaveKeys;
	private Map<String, Set<String>> threadKeys;
	private Map<String, String> allSlaveNames;

	public AllSlavesNotification(String masterName, Set<String> allNodeKeys, Map<String, Set<String>> threadKeys, Map<String, String> allSlaveNames)
	{
		super(ThreadingMessageType.ALL_SLAVES_NOTIFICATION);
		this.masterName = masterName;
		this.allSlaveKeys = allNodeKeys;
		this.threadKeys = threadKeys;
		this.allSlaveNames = allSlaveNames;
	}
	
	public String getMasterName()
	{
		return this.masterName;
	}

	public Set<String> getAllSlaveKeys()
	{
		return this.allSlaveKeys;
	}
	
	public Map<String, Set<String>> getThreadKeys()
	{
		return this.threadKeys;
	}
	
	public Map<String, String> getAllSlaveNames()
	{
		return this.allSlaveNames;
	}
}
