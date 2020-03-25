package org.greatfree.dip.cps.cache.message.replicate;

import org.greatfree.dip.cps.cache.data.MyPointing;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/19/2018, Bing Li
public class ReplicateMyPointingMapNotification extends ServerMessage
{
	private static final long serialVersionUID = -5075167684643063787L;
	
	private MyPointing pointing;

	public ReplicateMyPointingMapNotification(MyPointing pointing)
	{
		super(TestCacheMessageType.REPLICATE_MY_POINTING_NOTIFICATION);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
