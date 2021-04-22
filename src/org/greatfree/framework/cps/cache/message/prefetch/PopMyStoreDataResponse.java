package org.greatfree.framework.cps.cache.message.prefetch;

import java.util.List;

import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/09/2018, Bing Li
public class PopMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = 4706232893656572404L;
	
	private List<MyStoreData> data;

	public PopMyStoreDataResponse(List<MyStoreData> data)
	{
		super(TestCacheMessageType.POP_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public List<MyStoreData> getData()
	{
		return this.data;
	}
}
