package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class CachePointingByIndexRequest extends ServerMessage
{
	private static final long serialVersionUID = -6052616922604021969L;
	
	private String mapKey;
	private int index;
	private boolean isTiming;

	public CachePointingByIndexRequest(String mapKey, int index, boolean isTiming)
	{
		super(TestCacheMessageType.CACHE_POINTING_BY_INDEX_REQUEST);
		this.mapKey = mapKey;
		this.index = index;
		this.isTiming = isTiming;
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
