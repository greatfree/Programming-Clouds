package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/05/2018, Bing Li
public class TopReadCachePointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = 2057011071639724656L;
	
	private String mapKey;
	private int endIndex;

	public TopReadCachePointingsRequest(String mapKey, int endIndex)
	{
		super(TestCacheMessageType.TOP_READ_CACHE_POINTINGS_REQUEST);
		this.mapKey = mapKey;
		this.endIndex = endIndex;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
}
