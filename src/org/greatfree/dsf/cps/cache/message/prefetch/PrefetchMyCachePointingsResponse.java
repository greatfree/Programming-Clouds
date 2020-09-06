package org.greatfree.dsf.cps.cache.message.prefetch;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCacheTiming;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class PrefetchMyCachePointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = -6654984584501532048L;
	
	private List<MyCachePointing> pointings;
	private List<MyCacheTiming> timings;

	public PrefetchMyCachePointingsResponse(List<MyCachePointing> pointings)
	{
		super(TestCacheMessageType.PREFETCH_MY_CACHE_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public PrefetchMyCachePointingsResponse(String key, List<MyCacheTiming> timings)
	{
		super(TestCacheMessageType.PREFETCH_MY_CACHE_POINTINGS_RESPONSE);
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
