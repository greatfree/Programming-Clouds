package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/31/2018, Bing Li
public class SaveMyPointingsMapNotification extends ServerMessage
{
	private static final long serialVersionUID = 1177830032450051561L;
	
	private List<MyPointing> pointings;

	public SaveMyPointingsMapNotification(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.SAVE_MY_POINTINGS_MAP_NOTIFICATION);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
