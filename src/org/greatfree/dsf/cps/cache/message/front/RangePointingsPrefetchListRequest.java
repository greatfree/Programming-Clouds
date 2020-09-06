package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/03/2018, Bing Li
public class RangePointingsPrefetchListRequest extends ServerMessage
{
	private static final long serialVersionUID = 2067280492008860127L;

	private String listKey;
	private int startIndex;
	private int endIndex;

	public RangePointingsPrefetchListRequest(String listKey, int startIndex, int endIndex)
	{
		super(TestCacheMessageType.RANGE_POINTINGS_PREFETCH_LIST_REQUEST);
		this.listKey = listKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public String getMapKey()
	{
		return this.listKey;
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
