package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/27/2018, Bing Li
public class ShutdownBusinessServerNotification extends ServerMessage
{
	private static final long serialVersionUID = 4051259788129586720L;
	
	public ShutdownBusinessServerNotification()
	{
		super(BusinessMessageType.SHUTDOWN_BUSINESS_SERVER_NOTIFICATION);
	}

}
