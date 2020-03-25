package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class PostfetchMyCachePointingByIndexResponse extends ServerMessage
{
	private static final long serialVersionUID = -6869785850634121130L;
	
	private MyCachePointing pointing;
	private MyCacheTiming timing;

	public PostfetchMyCachePointingByIndexResponse(MyCachePointing pointing)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_INDEX_RESPONSE);
		this.pointing = pointing;
	}

	public PostfetchMyCachePointingByIndexResponse(MyCacheTiming timing)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_INDEX_RESPONSE);
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
