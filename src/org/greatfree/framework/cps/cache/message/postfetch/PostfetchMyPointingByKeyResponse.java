package org.greatfree.framework.cps.cache.message.postfetch;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/12/2018, Bing Li
public class PostfetchMyPointingByKeyResponse extends ServerMessage
{
	private static final long serialVersionUID = 4340706027060768145L;
	
	private MyPointing pointing;

	public PostfetchMyPointingByKeyResponse(MyPointing pointing)
	{
		super(TestCacheMessageType.POSTFETCH_MY_POINTING_BY_KEY_RESPONSE);
		this.pointing = pointing;
	}
	
	public MyPointing getPointing()
	{
		return this.pointing;
	}

}
