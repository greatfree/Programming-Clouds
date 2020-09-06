package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCacheTiming;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class MaxCachePointingResponse extends ServerMessage
{
	private static final long serialVersionUID = -6152735880937551084L;
	
	private MyCachePointing pointing;
	private MyCacheTiming timing;

	public MaxCachePointingResponse(MyCachePointing pointing)
	{
		super(TestCacheMessageType.MAX_CACHE_POINTING_RESPONSE);
		this.pointing = pointing;
	}

	public MaxCachePointingResponse(MyCacheTiming timing)
	{
		super(TestCacheMessageType.MAX_CACHE_POINTING_RESPONSE);
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
