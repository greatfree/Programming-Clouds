package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataKeysRequest extends ServerMessage
{
	private static final long serialVersionUID = 3842847528255488494L;
	
	private String mapKey;
	private int size;

	public PostfetchMyStoreDataKeysRequest(String mapKey, int size)
	{
		super(TestCacheMessageType.POSTFETCH_MY_STORE_DATA_KEYS_REQUEST);
		this.mapKey = mapKey;
		this.size = size;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}

	public int getSize()
	{
		return this.size;
	}
}
