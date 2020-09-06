package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/09/2018, Bing Li
public class PeekMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = 4933959290973880733L;
	
	private List<MyStoreData> data;
	private MyStoreData v;

	public PeekMyStoreDataResponse(List<MyStoreData> data)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public PeekMyStoreDataResponse(MyStoreData data)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_RESPONSE);
		this.v = data;
	}

	public List<MyStoreData> getData()
	{
		return this.data;
	}
	
	public MyStoreData getValue()
	{
		return this.v;
	}
}
