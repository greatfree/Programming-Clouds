package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/05/2018, Bing Li
public class IsCacheReadExistedInPointingStoreResponse extends ServerMessage
{
	private static final long serialVersionUID = -2925012327273568155L;

	private boolean isExisted;

	public IsCacheReadExistedInPointingStoreResponse(boolean isExisted)
	{
		super(TestCacheMessageType.IS_CACHE_READ_EXISTED_RESPONSE);
		this.isExisted = isExisted;
	}

	public boolean isExisted()
	{
		return this.isExisted;
	}
}
