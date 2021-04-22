package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/20/2018, Bing Li
public class LoadMaxMyPointingResponse extends ServerMessage
{
	private static final long serialVersionUID = 1797403959153496169L;
	
	private MyPointing pointing;

	public LoadMaxMyPointingResponse(MyPointing pointing)
	{
		super(TestCacheMessageType.LOAD_MAX_MY_POINTING_RESPONSE);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
