package org.greatfree.dsf.cps.cache.message.postfetch;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/12/2018, Bing Li
public class PostfetchMyPointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = 8732123683037081064L;
	
	private int startIndex;
	private int endIndex;
//	private int postfetchCount;

//	public PostfetchMyPointingsRequest(int index, int postfetchCount)
	public PostfetchMyPointingsRequest(int startIndex, int endIndex)
	{
		super(TestCacheMessageType.POSTFETCH_MY_POINTINGS_REQUEST);
//		this.index = index;
//		this.postfetchCount = postfetchCount;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	/*
	public int getIndex()
	{
		return this.index;
	}
	
	public int getPostfetchCount()
	{
		return this.postfetchCount;
	}
	*/
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
}
