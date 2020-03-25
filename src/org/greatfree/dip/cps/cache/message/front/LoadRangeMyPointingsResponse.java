package org.greatfree.dip.cps.cache.message.front;

import java.util.List;

import org.greatfree.dip.cps.cache.data.MyPointing;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/13/2018, Bing Li
public class LoadRangeMyPointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = -2205650396817140856L;
	
	private List<MyPointing> pointings;

	public LoadRangeMyPointingsResponse(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.LOAD_RANGE_MY_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
