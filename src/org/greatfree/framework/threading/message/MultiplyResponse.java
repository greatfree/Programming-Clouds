package org.greatfree.framework.threading.message;

import org.greatfree.concurrency.threading.message.TaskResponse;

// Created: 10/05/2019, Bing Li
public class MultiplyResponse extends TaskResponse
{
	private static final long serialVersionUID = -2621012166319934956L;
	
	private int result;

	public MultiplyResponse(int result, String collaboratorKey)
	{
		super(collaboratorKey);
		this.result = result;
	}

	public int getResult()
	{
		return this.result;
	}
}
