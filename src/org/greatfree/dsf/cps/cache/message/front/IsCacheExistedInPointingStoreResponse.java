package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class IsCacheExistedInPointingStoreResponse extends ServerMessage
{
	private static final long serialVersionUID = -6861979361339112780L;
	
	private boolean isExisted;

	public IsCacheExistedInPointingStoreResponse(boolean isExisted)
	{
		super(TestCacheMessageType.IS_CACHE_EXISTED_RESPONSE);
		this.isExisted = isExisted;
	}

	public boolean isExisted()
	{
		return this.isExisted;
	}
}
