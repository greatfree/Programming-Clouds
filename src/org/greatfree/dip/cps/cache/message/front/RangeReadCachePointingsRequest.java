package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/05/2018, Bing Li
public class RangeReadCachePointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = -4816760948825748484L;

	private String mapKey;
	private int startIndex;
	private int endIndex;

	public RangeReadCachePointingsRequest(String mapKey, int startIndex, int endIndex)
	{
		super(TestCacheMessageType.RANGE_READ_CACHE_POINTINGS_REQUEST);
		this.mapKey = mapKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
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
}
