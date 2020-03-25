package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class DequeueSingleMyStoreDataRequest extends ServerMessage
{
	private static final long serialVersionUID = -3509261204877727705L;
	
	private String queueKey;

	public DequeueSingleMyStoreDataRequest(String queueKey)
	{
		super(TestCacheMessageType.DEQUEUE_SINGLE_MY_STORE_DATA_REQUEST);
		this.queueKey = queueKey;
	}

	public String getQueueKey()
	{
		return this.queueKey;
	}
}
