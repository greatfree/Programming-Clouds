package org.greatfree.dsf.cps.cache.message.postfetch;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class PostfetchMyCachePointingByIndexRequest extends ServerMessage
{
	private static final long serialVersionUID = -1736748263902273391L;
	
	private String mapKey;
	private int index;
	
	private boolean isTiming;
	
	private boolean isTerminalMap;

	public PostfetchMyCachePointingByIndexRequest(String mapKey, int index, boolean isTiming, boolean isTerminalMap)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_INDEX_REQUEST);
		this.mapKey = mapKey;
		this.index = index;
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
	
	public int getIndex()
	{
		return this.index;
	}
	
	public boolean isTiming()
	{
		return this.isTiming;
	}
}
