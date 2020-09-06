package org.greatfree.dsf.cps.cache.message.postfetch;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = -565656093628914802L;
	
	private String mapKey;
	private int startIndex;
	private int endIndex;
	
	private boolean isTiming;
	
	private boolean isTerminalMap;

	public PostfetchMyCachePointingsRequest(String mapKey, int startIndex, int endIndex, boolean isTiming, boolean isTerminalMap)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTINGS_REQUEST);
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
