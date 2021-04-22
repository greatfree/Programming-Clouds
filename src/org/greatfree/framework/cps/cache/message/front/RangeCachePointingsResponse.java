package org.greatfree.framework.cps.cache.message.front;

import java.util.List;

import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.data.MyCacheTiming;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class RangeCachePointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = -2814831342859761397L;
	
	private List<MyCachePointing> pointings;
	private List<MyCacheTiming> timings;

	public RangeCachePointingsResponse(List<MyCachePointing> pointings)
	{
		super(TestCacheMessageType.RANGE_CACHE_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public RangeCachePointingsResponse(String key, List<MyCacheTiming> timings)
	{
		super(TestCacheMessageType.RANGE_CACHE_POINTINGS_RESPONSE);
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
