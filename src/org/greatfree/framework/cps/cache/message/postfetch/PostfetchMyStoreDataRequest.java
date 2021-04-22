package org.greatfree.framework.cps.cache.message.postfetch;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataRequest extends ServerMessage
{
	private static final long serialVersionUID = 9153489792203058050L;
	
	private String mapKey;
	private String dataKey;

	public PostfetchMyStoreDataRequest(String mapKey, String dataKey)
	{
		super(TestCacheMessageType.POSTFETCH_MY_STORE_DATA_REQUEST);
		this.mapKey = mapKey;
		this.dataKey = dataKey;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}

	public String getDataKey()
	{
		return this.dataKey;
	}
}
