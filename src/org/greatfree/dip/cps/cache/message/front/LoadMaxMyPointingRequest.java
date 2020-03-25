package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/20/2018, Bing Li
public class LoadMaxMyPointingRequest extends ServerMessage
{
	private static final long serialVersionUID = 4947485868032959694L;
	
	public LoadMaxMyPointingRequest()
	{
		super(TestCacheMessageType.LOAD_MAX_MY_POINTING_REQUEST);
	}

}
