package org.greatfree.framework.cps.cache.message.replicate;

import java.util.List;

import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class PushMuchMyStoreDataNotification extends ServerMessage
{
	private static final long serialVersionUID = -3503455667944365816L;
	
	private String stackKey;
	private List<MyStoreData> data;

	public PushMuchMyStoreDataNotification(String stackKey, List<MyStoreData> data)
	{
		super(TestCacheMessageType.PUSH_MUCH_MY_STORE_DATA_NOTIFICATION);
		this.stackKey = stackKey;
		this.data = data;
	}
	
	public String getStackKey()
	{
		return this.stackKey;
	}

	public List<MyStoreData> getData()
	{
		return this.data;
	}
}
