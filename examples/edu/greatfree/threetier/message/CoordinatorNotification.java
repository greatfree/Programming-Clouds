package edu.greatfree.threetier.message;

import org.greatfree.message.ServerMessage;

// Created: 07/06/2018, Bing Li
public class CoordinatorNotification extends ServerMessage
{
	private static final long serialVersionUID = 2657335756608511424L;
	
	private String notification;

	public CoordinatorNotification(String notification)
	{
		super(CPSMessageType.COORDINATOR_NOTIFICATION);
		this.notification = notification;
	}

	public String getNotification()
	{
		return this.notification;
	}
}
