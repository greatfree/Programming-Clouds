package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataResponse extends ServerMessage
{
	private static final long serialVersionUID = -6217255601205056622L;
	
	private MyData myData;

	public PostfetchMyDataResponse(MyData myData)
	{
		super(TestCacheMessageType.POSTFETCH_MY_DATA_RESPONSE);
		this.myData = myData;
	}

	public MyData getMyData()
	{
		return this.myData;
	}
}
