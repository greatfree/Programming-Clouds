package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.UtilConfig;

// Created: 08/13/2018, Bing Li
public class PeekMyStoreDataQueueRequest extends ServerMessage
{
	private static final long serialVersionUID = -1494088081815721815L;
	
	private String queueKey;
	private int count;
	
	private int startIndex;
	private int endIndex;

	public PeekMyStoreDataQueueRequest(String queueKey, int count)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_QUEUE_REQUEST);
		this.queueKey = queueKey;
		this.count = count;
		this.startIndex = UtilConfig.NO_INDEX;
		this.endIndex = UtilConfig.NO_INDEX;
	}

	public PeekMyStoreDataQueueRequest(String queueKey, int startIndex, int endIndex)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_QUEUE_REQUEST);
		this.queueKey = queueKey;
		this.count = endIndex - startIndex + 1;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public PeekMyStoreDataQueueRequest(int index, String queueKey)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_QUEUE_REQUEST);
		this.queueKey = queueKey;
		this.count = 1;
		this.startIndex = index;
		this.endIndex = index;
	}

	public String getQueueKey()
	{
		return this.queueKey;
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
}
