package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCacheTiming;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class SaveCachePointingsNotification extends ServerMessage
{
	private static final long serialVersionUID = 3036735756376126250L;

	private String cacheKey;
	private List<MyCachePointing> pointings;
	private List<MyCacheTiming> timings;

	public SaveCachePointingsNotification(String cacheKey, List<MyCachePointing> pointings)
	{
		super(TestCacheMessageType.SAVE_MY_CACHE_POINTINGS_MAP_NOTIFICATION);
		this.cacheKey = cacheKey;
		this.pointings = pointings;
	}

	public SaveCachePointingsNotification(List<MyCacheTiming> timings, String cacheKey)
	{
		super(TestCacheMessageType.SAVE_MY_CACHE_POINTINGS_MAP_NOTIFICATION);
		this.cacheKey = cacheKey;
		this.timings = timings;
	}

	public String getCacheKey()
	{
		return this.cacheKey;
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
