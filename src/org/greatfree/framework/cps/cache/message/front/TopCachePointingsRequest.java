package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class TopCachePointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = 2462875964841413092L;
	
	private String mapKey;
	private int endIndex;
	private boolean isTiming;

	public TopCachePointingsRequest(String mapKey, int endIndex, boolean isTiming)
	{
		super(TestCacheMessageType.TOP_CACHE_POINTINGS_REQUEST);
		this.mapKey = mapKey;
		this.endIndex = endIndex;
		this.isTiming = isTiming;
	}

	public String getMapKey()
	{
		return this.mapKey;
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
