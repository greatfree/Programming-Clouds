package org.greatfree.framework.cps.cache.message.front;

import java.util.List;

import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/28/2018, Bing Li
public class SaveMyPointingsListNotification extends ServerMessage
{
	private static final long serialVersionUID = 8366750471315194625L;
	
	private List<MyPointing> pointings;

	public SaveMyPointingsListNotification(List<MyPointing> pointings)
	{
		super(TestCacheMessageType.SAVE_MY_POINTINGS_LIST_NOTIFICATION);
		this.pointings = pointings;
	}

	public List<MyPointing> getPointings()
	{
		return this.pointings;
	}
}
