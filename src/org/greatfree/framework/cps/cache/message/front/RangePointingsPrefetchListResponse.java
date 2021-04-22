package org.greatfree.framework.cps.cache.message.front;

import java.util.List;

import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/03/2018, Bing Li
public class RangePointingsPrefetchListResponse extends ServerMessage
{
	private static final long serialVersionUID = -3668397228196033189L;

	private List<MyCachePointing> pointings;

	public RangePointingsPrefetchListResponse(List<MyCachePointing> pointings)
	{
		super(TestCacheMessageType.RANGE_POINTINGS_PREFETCH_LIST_RESPONSE);
		this.pointings = pointings;
	}

	public List<MyCachePointing> getPointings()
	{
		return this.pointings;
	}
}
