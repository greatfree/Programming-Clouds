package org.greatfree.dsf.cps.cache.message.replicate;

import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/11/2018, Bing Li
public class ReplicateMyPointingNotification extends ServerMessage
{
	private static final long serialVersionUID = -2436852275209846410L;
	
	private MyPointing pointing;
	
	public ReplicateMyPointingNotification(MyPointing pointing)
	{
		super(TestCacheMessageType.REPLICATE_MY_POINTING_NOTIFICATION);
		this.pointing = pointing;
	}
	
	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
