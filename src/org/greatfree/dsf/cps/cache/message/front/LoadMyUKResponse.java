package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/28/2019, Bing Li
public class LoadMyUKResponse extends ServerMessage
{
	private static final long serialVersionUID = -3055266666236382663L;
	
	private MyUKValue uk;

	public LoadMyUKResponse(MyUKValue uk)
	{
		super(TestCacheMessageType.LOAD_MY_UK_RESPONSE);
		this.uk = uk;
	}

	public MyUKValue getUK()
	{
		return this.uk;
	}
}
