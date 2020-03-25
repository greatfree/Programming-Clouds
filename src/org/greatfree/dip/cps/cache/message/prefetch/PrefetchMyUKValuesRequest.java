package org.greatfree.dip.cps.cache.message.prefetch;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 02/25/2019, Bing Li
public class PrefetchMyUKValuesRequest extends ServerMessage
{
	private static final long serialVersionUID = 5231357790666288663L;
	
	private int startIndex;
	private int endIndex;

	public PrefetchMyUKValuesRequest(int startIndex, int endIndex)
	{
		super(TestCacheMessageType.PREFETCH_MY_UK_VALUES_REQUEST);
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
