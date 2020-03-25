package org.greatfree.dip.threading.message;

import java.util.Set;

import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.dip.threading.TaskConfig;

// Created: 09/28/2019, Bing Li
public class AddRequest extends TaskRequest
{
	private static final long serialVersionUID = -7945266421136739637L;
	
	private int x;
	private int y;

	public AddRequest(String threadKey, int x, int y)
	{
		super(threadKey, TaskConfig.ADD_TASK_KEY);
		this.x = x;
		this.y = y;
	}
	
	public AddRequest(Set<String> threadKeys, int x, int y)
	{
		super(threadKeys, TaskConfig.ADD_TASK_KEY);
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
