package org.greatfree.testing.cache.distributed;

import org.greatfree.abandoned.cache.distributed.MapValueReceivable;

// Created: 07/04/2017, Bing Li
public class MyMap implements MapValueReceivable<String, TestPointing>
{
	private String key;
	
	public MyMap(String key)
	{
		this.key = key;
	}

	@Override
	public void valueReceived(String key, TestPointing value)
	{
		System.out.println(this.key);
	}

	public String getKey()
	{
		return this.key;
	}

}
