package org.greatfree.framework.threading.message;

import java.util.Set;

import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.framework.threading.TaskConfig;

// Created: 10/05/2019, Bing Li
public class MultiplyRequest extends TaskInvokeRequest
{
	private static final long serialVersionUID = 1359522776839394591L;
	
	private int x;
	private int y;

	public MultiplyRequest(String threadKey, int x, int y)
	{
		super(threadKey, TaskConfig.MULTIPLY_TASK_KEY);
		this.x = x;
		this.y = y;
	}

	public MultiplyRequest(Set<String> threadKeys, int x, int y)
	{
		super(threadKeys, TaskConfig.MULTIPLY_TASK_KEY);
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
