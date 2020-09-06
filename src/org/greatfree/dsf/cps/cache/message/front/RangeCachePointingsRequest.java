package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class RangeCachePointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = -8004606989983858042L;
	
	private String mapKey;
	private int startIndex;
	private int endIndex;
	private boolean isTiming;

	public RangeCachePointingsRequest(String mapKey, int startIndex, int endIndex, boolean isTiming)
	{
		super(TestCacheMessageType.RANGE_CACHE_POINTINGS_REQUEST);
		this.mapKey = mapKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.isTiming = isTiming;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
	
	public boolean isTiming()
	{
		return this.isTiming;
	}
}
