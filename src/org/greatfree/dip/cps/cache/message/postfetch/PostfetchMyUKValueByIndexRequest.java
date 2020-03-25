package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 02/25/2019, Bing Li
public class PostfetchMyUKValueByIndexRequest extends ServerMessage
{
	private static final long serialVersionUID = -1091929449256204357L;
	
	private int index;

	public PostfetchMyUKValueByIndexRequest(int index)
	{
		super(TestCacheMessageType.POSTFETCH_MY_UK_BY_INDEX_REQUEST);
		this.index = index;
	}

	public int getIndex()
	{
		return this.index;
	}
}