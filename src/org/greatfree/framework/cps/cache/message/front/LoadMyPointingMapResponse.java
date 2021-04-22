package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/20/2018, Bing Li
public class LoadMyPointingMapResponse extends ServerMessage
{
	private static final long serialVersionUID = -9135329498370944555L;

	private MyPointing pointing;

	public LoadMyPointingMapResponse(MyPointing pointing)
	{
		super(TestCacheMessageType.LOAD_MY_POINTING_MAP_RESPONSE);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
