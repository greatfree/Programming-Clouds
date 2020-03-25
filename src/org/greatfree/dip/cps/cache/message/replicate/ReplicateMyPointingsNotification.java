package org.greatfree.dip.cps.cache.message.replicate;

import java.util.List;

import org.greatfree.dip.cps.cache.data.MyPointing;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/11/2018, Bing Li
public class ReplicateMyPointingsNotification extends ServerMessage
{
	private static final long serialVersionUID = -759204461353010333L;
	
	private List<MyPointing> pointings;

	public ReplicateMyPointingsNotification(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.REPLICATE_MY_POINTINGS_LIST_NOTIFICATION);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
