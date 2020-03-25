package org.greatfree.testing.cache.distributed;

import org.greatfree.abandoned.cache.distributed.CacheKey;

// Created: 07/17/2017, Bing Li
//public class IntegerValue extends CacheValue
public class IntegerValue extends CacheKey<String>
{
	private static final long serialVersionUID = -8324676084637456138L;
	
	private Integer v;

	public IntegerValue(String dataKey, Integer v)
	{
		super(dataKey);
		this.v = v;
	}

	public Integer getValue()
	{
		return this.v;
	}
}
