package org.greatfree.dsf.cps.cache.message.postfetch;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/12/2018, Bing Li
public class PostfetchMyPointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = 7751264895290059517L;
	
	private List<MyPointing> pointings;

	public PostfetchMyPointingsResponse(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.POSTFETCH_MY_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
