package org.greatfree.dip.cps.cache.message.replicate;

import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/08/2018, Bing Li
public class ReplicateMyDataNotification extends ServerMessage
{
	private static final long serialVersionUID = 1764965555213548855L;
	
	private MyData data;

	public ReplicateMyDataNotification(MyData data)
	{
		super(TestCacheMessageType.REPLICATE_MY_DATA_NOTIFICATION);
		this.data =  data;
	}

	public MyData getData()
	{
		return this.data;
	}
}
