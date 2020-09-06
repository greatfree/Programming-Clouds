package org.greatfree.dsf.threading.message;

import java.util.Set;

import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.dsf.threading.TaskConfig;

// Created: 10/05/2019, Bing Li
public class AddInterNotification extends InteractNotification
{
	private static final long serialVersionUID = -6951253893245234986L;
	
	private int x;
	private int y;

	public AddInterNotification(String slaveKey, String threadKey, int x, int y)
	{
		super(slaveKey, threadKey, TaskConfig.ADD_TASK_KEY);
		this.x = x;
		this.y = y;
	}
	
	public AddInterNotification(String slaveKey, Set<String> threadKeys, int x, int y)
	{
		super(slaveKey, threadKeys, TaskConfig.ADD_TASK_KEY);
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
}
