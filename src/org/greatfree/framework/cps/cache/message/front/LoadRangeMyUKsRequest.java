package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 02/28/2019, Bing Li
public class LoadRangeMyUKsRequest extends ServerMessage
{
	private static final long serialVersionUID = -8012002901152751389L;
	
	private int startIndex;
	private int endIndex;

	public LoadRangeMyUKsRequest(int startIndex, int endIndex)
	{
		super(TestCacheMessageType.LOAD_RANGE_MY_UKS_REQUEST);
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
