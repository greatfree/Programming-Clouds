package org.greatfree.framework.cps.cache.message.prefetch;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/11/2018, Bing Li
public class PrefetchMyPointingsRequest extends ServerMessage
{
	private static final long serialVersionUID = -1190573665681894446L;
	
	private int startIndex;
	private int endIndex;

	public PrefetchMyPointingsRequest(int startIndex, int endIndex)
	{
		super(TestCacheMessageType.PREFETCH_MY_POINTINGS_REQUEST);
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
