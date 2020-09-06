package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/03/2018, Bing Li
public class TopPointingsPrefetchListRequest extends ServerMessage
{
	private static final long serialVersionUID = 2909946490360003335L;
	
	private String listKey;
	private int endIndex;

	public TopPointingsPrefetchListRequest(String listKey, int endIndex)
	{
		super(TestCacheMessageType.TOP_POINTINGS_PREFETCH_LIST_REQUEST);
		this.listKey = listKey;
		this.endIndex = endIndex;
	}

	public String getListKey()
	{
		return this.listKey;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
}
