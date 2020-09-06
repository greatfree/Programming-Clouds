package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/13/2018, Bing Li
public class LoadRangeMyPointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = -6047245665859861282L;
	
	private int startIndex;
	private int endIndex;

	public LoadRangeMyPointingsRequest(int startIndex, int endIndex)
	{
		super(TestCacheMessageType.LOAD_RANGE_MY_POINTINGS_REQUEST);
		this.startIndex = startIndex;
		this.endIndex = endIndex;
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
