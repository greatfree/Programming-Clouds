package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.data.MyPointing;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/16/2018, Bing Li
public class PostfetchMyPointingByIndexResponse extends ServerMessage
{
	private static final long serialVersionUID = 6913638640110016485L;
	
	private MyPointing pointing;

	public PostfetchMyPointingByIndexResponse(MyPointing pointing)
	{
		super(TestCacheMessageType.POSTFETCH_MY_POINTING_BY_INDEX_RESPONSE);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
