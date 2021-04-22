package org.greatfree.framework.threading.message;

import org.greatfree.concurrency.threading.message.TaskResponse;

// Created: 09/28/2019, Bing Li
public class AddResponse extends TaskResponse
{
	private static final long serialVersionUID = -7095134472419902451L;
	
	private int sum;

	public AddResponse(int sum, String collaboratorKey)
	{
		super(collaboratorKey);
		this.sum = sum;
	}

	public int getSum()
	{
		return this.sum;
	}
}
