package edu.greatfree.threetier.message;

import org.greatfree.message.ServerMessage;

// Created: 07/06/2018, Bing Li
public class FrontNotification extends ServerMessage
{
	private static final long serialVersionUID = -5407675461769163152L;
	
	private String notification;

	public FrontNotification(String notification)
	{
		super(CPSMessageType.FRONT_NOTIFICATION);
		this.notification = notification;
	}

	public String getNotification()
	{
		return this.notification;
	}
}
