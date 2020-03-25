package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class CachePointingByKeyResponse extends ServerMessage
{
	private static final long serialVersionUID = -8230633753445528902L;
	
	private MyCachePointing pointing;
	private MyCacheTiming timing;

	public CachePointingByKeyResponse(MyCachePointing pointing)
	{
		super(TestCacheMessageType.CACHE_POINTING_BY_KEY_RESPONSE);
		this.pointing = pointing;
	}

	public CachePointingByKeyResponse(MyCacheTiming timing)
	{
		super(TestCacheMessageType.CACHE_POINTING_BY_KEY_RESPONSE);
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
