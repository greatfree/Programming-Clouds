package org.greatfree.concurrency.threading.message;

import java.util.Set;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.message.container.Notification;

// Created: 09/11/2019, Bing Li
public abstract class InstructNotification extends Notification
{
	private static final long serialVersionUID = 6669988461756815216L;
	
//	private String slaveKey;
	private String threadKey;
	private Set<String> threadKeys;
	
//	private String instructKey;

	/*
	public InstructNotification(int instruction)
	{
		super(instruction);
		this.threadKey = ThreadConfig.NO_THREAD_KEY;
		this.threadKeys = ThreadConfig.NO_THREAD_KEYS;
	}
	*/

	public InstructNotification(String threadKey, int instruction)
	{
		super(instruction);
//		this.slaveKey = slaveKey;
		this.threadKey = threadKey;
		this.threadKeys = ThreadConfig.NO_THREAD_KEYS;
//		this.instructKey = Tools.generateUniqueKey();
	}
	
	public InstructNotification(Set<String> threadKeys, int instruction)
	{
		super(instruction);
		this.threadKey = ThreadConfig.NO_THREAD_KEY;
		this.threadKeys = threadKeys;
//		this.instructKey = Tools.generateUniqueKey();
	}

	/*
	public InstructNotification(String threadKey, int instruction)
	{
		super(instruction);
		this.threadKey = threadKey;
		this.taskKey = UtilConfig.EMPTY_STRING;
		this.instructKey = Tools.generateUniqueKey();
	}
	*/

	/*
	public String getSlaveKey()
	{
		return this.slaveKey;
	}
	*/
	
	public String getThreadKey()
	{
		return this.threadKey;
	}
	
	/*
	public void setThreadKey(String key)
	{
		this.threadKey = key;
	}
	*/
	
	public Set<String> getThreadKeys()
	{
		return this.threadKeys;
	}

	/*
	public String getInstructKey()
	{
		return this.instructKey;
	}
	*/
}
