package org.greatfree.dsf.cps.cache.message.replicate;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCacheTiming;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class ReplicateCachePointingsNotification extends ServerMessage
{
	private static final long serialVersionUID = -3503455667944365816L;
	
	private String mapKey;
	private List<MyCachePointing> pointings;
	private List<MyCacheTiming> timings;
	
	private boolean isTerminalMap;

	public ReplicateCachePointingsNotification(String mapKey, List<MyCachePointing> pointings, boolean isTerminalMap)
	{
		super(TestCacheMessageType.REPLICATE_CACHE_POINTINGS_NOTIFICATION);
		this.mapKey = mapKey;
		this.pointings = pointings;
		this.isTerminalMap = isTerminalMap;
	}

	public ReplicateCachePointingsNotification(List<MyCacheTiming> timings, String mapKey)
	{
		super(TestCacheMessageType.REPLICATE_CACHE_POINTINGS_NOTIFICATION);
		this.mapKey = mapKey;
		this.timings = timings;
	}
	
	public boolean isTerminalMap()
	{
		return this.isTerminalMap;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
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
