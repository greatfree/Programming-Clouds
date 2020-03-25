package org.greatfree.dip.cps.cache.message.replicate;

import java.util.Map;

import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/30/2018, Bing Li
public class ReplicateMuchMyDataNotification extends ServerMessage
{
	private static final long serialVersionUID = -4927689244708491031L;
	
	private Map<String, MyData> data;

	public ReplicateMuchMyDataNotification(Map<String, MyData> data)
	{
		super(TestCacheMessageType.REPLICATE_MUCH_MY_DATA_NOTIFICATION);
		this.data = data;
	}

	public Map<String, MyData> getData()
	{
		return this.data;
	}
}
