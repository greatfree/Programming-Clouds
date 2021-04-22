package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/28/2019, Bing Li
public class SaveMyUKNotification extends ServerMessage
{
	private static final long serialVersionUID = -2235439028402957869L;
	
	private MyUKValue uk;

	public SaveMyUKNotification(MyUKValue uk)
	{
		super(TestCacheMessageType.SAVE_MY_UK_NOTIFICATION);
		this.uk = uk;
	}

	public MyUKValue getUK()
	{
		return this.uk;
	}
}
