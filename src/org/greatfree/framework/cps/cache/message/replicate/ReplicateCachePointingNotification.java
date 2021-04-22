package org.greatfree.framework.cps.cache.message.replicate;

import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.data.MyCacheTiming;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class ReplicateCachePointingNotification extends ServerMessage
{
	private static final long serialVersionUID = 1147954574350949963L;

	private MyCachePointing pointing;
	private MyCacheTiming timing;
	
	private boolean isTerminalMap;
	
	public ReplicateCachePointingNotification(MyCachePointing pointing, boolean isTerminalMap)
	{
		super(TestCacheMessageType.REPLICATE_CACHE_POINTING_NOTIFICATION);
		this.pointing = pointing;
		this.isTerminalMap = isTerminalMap;
	}
	
	public boolean isTerminalMap()
	{
		return this.isTerminalMap;
	}

	public ReplicateCachePointingNotification(MyCacheTiming timing)
	{
		super(TestCacheMessageType.REPLICATE_CACHE_POINTING_NOTIFICATION);
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
