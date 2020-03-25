package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyPointing;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/20/2018, Bing Li
public class LoadMinMyPointingResponse extends ServerMessage
{
	private static final long serialVersionUID = 4879795187724044368L;
	
	private MyPointing pointing;

	public LoadMinMyPointingResponse(MyPointing pointing)
	{
		super(TestCacheMessageType.LOAD_MIN_MY_POINTING_RESPONSE);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
