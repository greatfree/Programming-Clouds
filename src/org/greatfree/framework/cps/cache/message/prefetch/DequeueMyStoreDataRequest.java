package org.greatfree.framework.cps.cache.message.prefetch;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.UtilConfig;

// Created: 08/13/2018, Bing Li
public class DequeueMyStoreDataRequest extends ServerMessage
{
	private static final long serialVersionUID = -4134406852271798785L;
	
	private String queueKey;
	private int count;

	private int startIndex;
	private int endIndex;
	
	private boolean isPeeking;

	public DequeueMyStoreDataRequest(String queueKey, int count, boolean isPeeking)
	{
		super(TestCacheMessageType.DEQUEUE_MY_STORE_DATA_REQUEST);
		this.queueKey = queueKey;
		this.count = count;
		this.isPeeking = isPeeking;
		this.startIndex = UtilConfig.NO_INDEX;
		this.endIndex = UtilConfig.NO_INDEX;
	}

	public DequeueMyStoreDataRequest(String queueKey, int startIndex, int endIndex)
	{
		super(TestCacheMessageType.DEQUEUE_MY_STORE_DATA_REQUEST);
		this.queueKey = queueKey;
		this.count = UtilConfig.NO_COUNT;
		this.isPeeking = true;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public String getQueueKey()
	{
		return this.queueKey;
	}
	
	public int getCount()
	{
		return this.count;
	}

	public boolean isPeeking()
	{
		return this.isPeeking;
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
