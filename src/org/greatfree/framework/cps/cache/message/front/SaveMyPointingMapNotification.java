package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/20/2018, Bing Li
public class SaveMyPointingMapNotification extends ServerMessage
{
	private static final long serialVersionUID = -4854755215669670823L;

	private MyPointing pointing;

	public SaveMyPointingMapNotification(MyPointing pointing)
	{
		super(TestCacheMessageType.SAVE_MY_POINTING_MAP_NOTIFICATION);
		this.pointing = pointing;
	}

	public MyPointing getPointing()
	{
		return this.pointing;
	}
}
