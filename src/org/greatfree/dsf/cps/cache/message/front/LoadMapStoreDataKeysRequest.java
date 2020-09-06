package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class LoadMapStoreDataKeysRequest extends ServerMessage
{
	private static final long serialVersionUID = -1330134040529976256L;
	
	private String mapKey;

	public LoadMapStoreDataKeysRequest(String mapKey)
	{
		super(TestCacheMessageType.LOAD_MAP_STORE_DATA_KEYS_REQUEST);
		this.mapKey = mapKey;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}
}
