package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 02/28/2019, Bing Li
public class LoadTopMyUKsRequest extends ServerMessage
{
	private static final long serialVersionUID = -5729169186557448566L;
	
	private int endIndex;

	public LoadTopMyUKsRequest(int endIndex)
	{
		super(TestCacheMessageType.LOAD_TOP_MY_UKS_REQUEST);
		this.endIndex = endIndex;
	}

	public int getEndIndex()
	{
		return this.endIndex;
	}
}
