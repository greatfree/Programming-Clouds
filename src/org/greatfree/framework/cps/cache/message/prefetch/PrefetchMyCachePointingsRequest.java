package org.greatfree.framework.cps.cache.message.prefetch;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class PrefetchMyCachePointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = 3977385935349281227L;
	
	private String mapKey;
	private int startIndex;
	private int endIndex;
	
	private boolean isTiming;
	
	private boolean isTerminalMap;

	public PrefetchMyCachePointingsRequest(String mapKey, int startIndex, int endIndex, boolean isTiming, boolean isTerminalMap)
	{
		super(TestCacheMessageType.PREFETCH_MY_CACHE_POINTINGS_REQUEST);
		this.mapKey = mapKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.isTiming = isTiming;
		this.isTerminalMap = isTerminalMap;
	}
	
	public boolean isTerminalMap()
	{
		return this.isTerminalMap;
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
