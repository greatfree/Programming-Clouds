package org.greatfree.exceptions;

// Created: 04/08/2017, Bing Li
public class StoreOverflowException extends Exception
{
	private static final long serialVersionUID = -2606553409228576485L;
	
	private String cacheKey;

	public StoreOverflowException(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}

	public String getCacheKey()
	{
		return this.cacheKey;
	}
}
