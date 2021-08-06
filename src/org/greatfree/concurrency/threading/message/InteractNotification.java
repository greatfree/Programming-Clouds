package org.greatfree.concurrency.threading.message;

import java.util.Set;

// Created: 10/04/2019, Bing Li
public abstract class InteractNotification extends ATMNotification
{
	private static final long serialVersionUID = -2694015088239620584L;
	
	private String slaveKey;
	private String taskKey;

	public InteractNotification(String slaveKey, String threadKey, String taskKey)
	{
		super(threadKey, ATMMessageType.INTERACT_NOTIFICATION);
		this.slaveKey = slaveKey;
		this.taskKey = taskKey;
	}

	public InteractNotification(String slaveKey, Set<String> threadKeys, String taskKey)
	{
		super(threadKeys, ATMMessageType.INTERACT_NOTIFICATION);
		this.slaveKey = slaveKey;
		this.taskKey = taskKey;
	}
	
	public String getSlaveKey()
	{
		return this.slaveKey;
	}
	
	public String getTaskKey()
	{
		return this.taskKey;
	}
}
