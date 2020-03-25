package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/03/2018, Bing Li
public class PointingByIndexPrefetchListRequest extends ServerMessage
{
	private static final long serialVersionUID = 575432009501591150L;
	
	private String listKey;
	private int index;

	public PointingByIndexPrefetchListRequest(String listKey, int index)
	{
		super(TestCacheMessageType.POINTING_BY_INDEX_PREFETCH_LIST_REQUEST);
		this.listKey = listKey;
		this.index = index;
	}
	
	public String getListKey()
	{
		return this.listKey;
	}
	
	public int getIndex()
	{
		return this.index;
	}
}
