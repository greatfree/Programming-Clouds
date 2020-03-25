package org.greatfree.message.container;

import org.greatfree.message.ServerMessage;
import org.greatfree.server.container.CSMessageType;

// Created: 12/18/2018, Bing Li
public abstract class Notification extends ServerMessage
{
	private static final long serialVersionUID = 5170476706540411845L;
	
	private int applicationID;

//	public Notification(int messageType, int applicationID)
	public Notification(int applicationID)
	{
		super(CSMessageType.NOTIFICATION);
		this.applicationID = applicationID;
	}

	public int getApplicationID()
	{
		return this.applicationID;
	}
}

