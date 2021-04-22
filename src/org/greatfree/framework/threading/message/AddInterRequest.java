package org.greatfree.framework.threading.message;

import java.util.Set;

import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.framework.threading.TaskConfig;

// Created: 10/05/2019, Bing Li
public class AddInterRequest extends InteractRequest
{
	private static final long serialVersionUID = -3318366433276663235L;
	
	private int x;
	private int y;

	public AddInterRequest(String srcSlaveKey, String dstSlaveKey, String threadKey, int x, int y)
	{
		super(srcSlaveKey, dstSlaveKey, threadKey, TaskConfig.ADD_TASK_KEY);
		this.x = x;
		this.y = y;
	}

	public AddInterRequest(String srcSlaveKey, String dstSlaveKey, Set<String> threadKeys, int x, int y)
	{
		super(srcSlaveKey, dstSlaveKey, threadKeys, TaskConfig.ADD_TASK_KEY);
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
