package org.greatfree.framework.cps.cache.message.prefetch;

import java.util.List;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/11/2018, Bing Li
public class PrefetchMyPointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = 7412584929812767777L;
	
	private List<MyPointing> pointings;

	public PrefetchMyPointingsResponse(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.PREFETCH_MY_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
