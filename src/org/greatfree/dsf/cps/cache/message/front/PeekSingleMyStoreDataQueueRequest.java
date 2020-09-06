package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class PeekSingleMyStoreDataQueueRequest extends ServerMessage
{
	private static final long serialVersionUID = 2504152955450117149L;
	
	private String queueKey;

	public PeekSingleMyStoreDataQueueRequest(String queueKey)
	{
		super(TestCacheMessageType.PEEK_SINGLE_MY_STORE_DATA_QUEUE_REQUEST);
		this.queueKey = queueKey;
	}

	public String getQueueKey()
	{
		return this.queueKey;
	}
}
