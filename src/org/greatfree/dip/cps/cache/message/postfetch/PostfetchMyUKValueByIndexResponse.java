package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class PostfetchMyUKValueByIndexResponse extends ServerMessage
{
	private static final long serialVersionUID = -5928529625061863424L;
	
	private MyUKValue v;

	public PostfetchMyUKValueByIndexResponse(MyUKValue v)
	{
		super(TestCacheMessageType.POSTFETCH_MY_UK_BY_INDEX_RESPONSE);
		this.v = v;
	}

	public MyUKValue getValue()
	{
		return this.v;
	}
}
