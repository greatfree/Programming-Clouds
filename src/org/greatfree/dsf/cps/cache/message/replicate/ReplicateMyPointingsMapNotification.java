package org.greatfree.dsf.cps.cache.message.replicate;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/19/2018, Bing Li
public class ReplicateMyPointingsMapNotification extends ServerMessage
{
	private static final long serialVersionUID = 2194255653913648303L;

	private List<MyPointing> pointings;

	public ReplicateMyPointingsMapNotification(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.REPLICATE_MY_POINTINGS_MAP_NOTIFICATION);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
