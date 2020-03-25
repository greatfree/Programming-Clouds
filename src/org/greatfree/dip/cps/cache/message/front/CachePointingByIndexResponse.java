package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class CachePointingByIndexResponse extends ServerMessage
{
	private static final long serialVersionUID = 8980015821988060440L;
	
	private MyCachePointing pointing;
	private MyCacheTiming timing;

	public CachePointingByIndexResponse(MyCachePointing pointing)
	{
		super(TestCacheMessageType.CACHE_POINTING_BY_INDEX_REQUEST);
		this.pointing = pointing;
	}

	public CachePointingByIndexResponse(MyCacheTiming timing)
	{
		super(TestCacheMessageType.CACHE_POINTING_BY_INDEX_REQUEST);
		this.timing = timing;
	}

	public MyCachePointing getPointing()
	{
		return this.pointing;
	}
	
	public MyCacheTiming getTiming()
	{
		return this.timing;
	}
}
