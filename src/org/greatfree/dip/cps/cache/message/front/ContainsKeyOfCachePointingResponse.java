package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class ContainsKeyOfCachePointingResponse extends ServerMessage
{
	private static final long serialVersionUID = -5864240609493455379L;
	
	private boolean isExisted;

	public ContainsKeyOfCachePointingResponse(boolean isExisted)
	{
		super(TestCacheMessageType.CONTAINS_KEY_OF_CACHE_POINTING_RESPONSE);
		this.isExisted = isExisted;
	}

	public boolean isExisted()
	{
		return this.isExisted;
	}
}
