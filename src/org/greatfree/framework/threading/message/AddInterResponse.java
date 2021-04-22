package org.greatfree.framework.threading.message;

import org.greatfree.concurrency.threading.message.TaskResponse;

// Created: 10/05/2019, Bing Li
public class AddInterResponse extends TaskResponse
{
	private static final long serialVersionUID = 8764031008318064394L;
	
	private int sum;

	public AddInterResponse(int sum, String collaboratorKey)
	{
		super(collaboratorKey);
		this.sum = sum;
	}

	public int getSum()
	{
		return this.sum;
	}
}
