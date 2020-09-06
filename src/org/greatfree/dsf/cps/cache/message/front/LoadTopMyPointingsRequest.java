package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/13/2018, Bing Li
public class LoadTopMyPointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = 6063459423520065354L;
	
	private int endIndex;

	public LoadTopMyPointingsRequest(int endIndex)
	{
		super(TestCacheMessageType.LOAD_TOP_MY_POINTINGS_REQUEST);
		this.endIndex = endIndex;
	}

	public int getEndIndex()
	{
		return this.endIndex;
	}
}
