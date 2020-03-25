package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/09/2018, Bing Li
public class PeekSingleMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = -6091546074649687923L;
	
	private MyStoreData data;

	public PeekSingleMyStoreDataResponse(MyStoreData data)
	{
		super(TestCacheMessageType.PEEK_SINGLE_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
