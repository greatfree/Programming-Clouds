package org.greatfree.dsf.cps.cache.message.postfetch;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataRequest extends ServerMessage
{
	private static final long serialVersionUID = -6525048409037551185L;
	
	private String myDataKey;

	public PostfetchMyDataRequest(String key)
	{
		super(TestCacheMessageType.POSTFETCH_MY_DATA_REQUEST);
		this.myDataKey = key;
	}

	public String getMyDataKey()
	{
		return this.myDataKey;
	}
}
