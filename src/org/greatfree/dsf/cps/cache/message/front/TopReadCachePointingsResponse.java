package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/05/2018, Bing Li
public class TopReadCachePointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = 6160186277441418548L;
	
	private List<MyCachePointing> pointings;

	public TopReadCachePointingsResponse(List<MyCachePointing> pointings)
	{
		super(TestCacheMessageType.TOP_READ_CACHE_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public List<MyCachePointing> getPointings()
	{
		return this.pointings;
	}
}
