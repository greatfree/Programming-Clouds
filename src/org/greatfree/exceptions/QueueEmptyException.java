package org.greatfree.exceptions;

// Created: 03/16/2019, Bing Li
public class QueueEmptyException extends Exception
{
	private static final long serialVersionUID = 7982761319433351401L;
	
	private String queueKey;
	
	public QueueEmptyException(String queueKey)
	{
		this.queueKey = queueKey;
	}

	public String getQueueKey()
	{
		return this.queueKey;
	}
}
