package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/13/2018, Bing Li
public class LoadTopMyPointingsResponse extends ServerMessage
{
	private static final long serialVersionUID = 8178122050556170295L;
	
	private List<MyPointing> pointings;

	public LoadTopMyPointingsResponse(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.LOAD_TOP_MY_POINTINGS_RESPONSE);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
