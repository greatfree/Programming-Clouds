package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/16/2018, Bing Li
public class PostfetchMyPointingByIndexRequest extends ServerMessage
{
	private static final long serialVersionUID = -5708718918179324867L;
	
	private int index;

	public PostfetchMyPointingByIndexRequest(int index)
	{
		super(TestCacheMessageType.POSTFETCH_MY_POINTING_BY_INDEX_REQUEST);
		this.index = index;
	}

	public int getIndex()
	{
		return this.index;
	}
}
