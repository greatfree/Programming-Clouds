package org.greatfree.framework.cps.cache.message.postfetch;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 02/25/2019, Bing Li
public class PostfetchMyUKValuesRequest extends ServerMessage
{
	private static final long serialVersionUID = 1896948804065929573L;

	private int startIndex;
	private int endIndex;

	public PostfetchMyUKValuesRequest(int startIndex, int endIndex)
	{
		super(TestCacheMessageType.POSTFETCH_MY_UKS_REQUEST);
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
