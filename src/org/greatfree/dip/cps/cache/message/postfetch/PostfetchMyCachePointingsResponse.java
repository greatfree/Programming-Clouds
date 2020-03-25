package org.greatfree.dip.cps.cache.message.postfetch;

import java.util.List;

import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = -714387339812070364L;
	
	private List<MyCachePointing> pointings;
	private List<MyCacheTiming> timings;

	public PostfetchMyCachePointingsResponse(List<MyCachePointing> pointings)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public PostfetchMyCachePointingsResponse(String key, List<MyCacheTiming> timings)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTINGS_RESPONSE);
		this.timings = timings;
	}

	public List<MyCachePointing> getPointings()
	{
		return this.pointings;
	}
	
	public List<MyCacheTiming> getTimings()
	{
		return this.timings;
	}
}
