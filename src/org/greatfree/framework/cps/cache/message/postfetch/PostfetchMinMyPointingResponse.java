package org.greatfree.framework.cps.cache.message.postfetch;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/01/2018, Bing Li
public class PostfetchMinMyPointingResponse extends ServerMessage
{
	private static final long serialVersionUID = -8274416314658013295L;
	
	private MyPointing pointing;

	public PostfetchMinMyPointingResponse(MyPointing pointing)
	{
		super(TestCacheMessageType.POSTFETCH_MIN_MY_POINTING_RESPONSE);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
