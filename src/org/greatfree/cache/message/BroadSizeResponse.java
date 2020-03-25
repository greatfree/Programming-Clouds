package org.greatfree.cache.message;

import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of BroadSizeRequest. 07/11/2017, Bing Li
 */

// Created: 07/11/2017, Bing Li
public class BroadSizeResponse extends MulticastResponse
{
	private static final long serialVersionUID = -8913408017282509101L;
	
	private long size;

	public BroadSizeResponse(String key, String collaboratorKey, long size)
	{
//		super(CacheMessageType.BROAD_SIZE_RESPONSE, key, collaboratorKey);
		super(CacheMessageType.BROAD_SIZE_RESPONSE, collaboratorKey);
		this.size = size;
	}

	public long getSize()
	{
		return this.size;
	}
}
