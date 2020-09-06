package org.greatfree.dsf.cps.cache.data;

import java.util.Date;

import org.greatfree.util.Pointing;

// Created: 08/18/2018, Bing Li
//public class MyCacheTiming extends CachePointing
public class MyCacheTiming extends Pointing
{
	private static final long serialVersionUID = -457168271852048110L;

	public MyCacheTiming(String cacheKey, String key, Date time)
	{
		super(cacheKey, key, time.getTime());
	}

}
