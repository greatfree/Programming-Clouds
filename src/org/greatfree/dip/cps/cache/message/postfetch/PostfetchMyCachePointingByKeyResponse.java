package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByKeyResponse extends ServerMessage
{
	private static final long serialVersionUID = -6027645596042084924L;
	
	private MyCachePointing pointing;
	private MyCacheTiming timing;

	public PostfetchMyCachePointingByKeyResponse(MyCachePointing pointing)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_KEY_RESPONSE);
		this.pointing = pointing;
	}

	public PostfetchMyCachePointingByKeyResponse(MyCacheTiming timing)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_KEY_RESPONSE);
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
