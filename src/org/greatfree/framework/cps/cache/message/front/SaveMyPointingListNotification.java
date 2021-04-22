package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/13/2018, Bing Li
public class SaveMyPointingListNotification extends ServerMessage
{
	private static final long serialVersionUID = -717617329176124550L;
	
	private MyPointing pointing;

	public SaveMyPointingListNotification(MyPointing pointing)
	{
		super(TestCacheMessageType.SAVE_MY_POINTING_LIST_NOTIFICATION);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
