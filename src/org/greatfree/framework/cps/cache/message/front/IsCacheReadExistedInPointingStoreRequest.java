package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/05/2018, Bing Li
public class IsCacheReadExistedInPointingStoreRequest extends ServerMessage
{
	private static final long serialVersionUID = 5530093961132124892L;

	private String mapKey;

	public IsCacheReadExistedInPointingStoreRequest(String mapKey)
	{
		super(TestCacheMessageType.IS_CACHE_READ_EXISTED_REQUEST);
		this.mapKey = mapKey;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}
}
