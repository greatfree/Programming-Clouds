package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/09/2018, Bing Li
public class PopSingleMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = -8684180843878036008L;
	
	private MyStoreData data;

	public PopSingleMyStoreDataResponse(MyStoreData data)
	{
		super(TestCacheMessageType.POP_SINGLE_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
