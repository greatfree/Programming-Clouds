package org.greatfree.framework.cps.cache.message.front;

import java.util.List;

import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/05/2018, Bing Li
public class RangeReadCachePointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = -3719799392340197483L;

	private List<MyCachePointing> pointings;

	public RangeReadCachePointingsResponse(List<MyCachePointing> pointings)
	{
		super(TestCacheMessageType.RANGE_READ_CACHE_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public List<MyCachePointing> getPointings()
	{
		return this.pointings;
	}
}
