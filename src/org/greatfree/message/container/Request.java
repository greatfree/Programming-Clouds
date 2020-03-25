package org.greatfree.message.container;

import org.greatfree.message.ServerMessage;
import org.greatfree.server.container.CSMessageType;

// Created: 12/18/2018, Bing Li
public abstract class Request extends ServerMessage
{
	private static final long serialVersionUID = -881223569797936953L;
	
	private int applicationID;

//	public Request(int messageType, int applicationID)
	public Request(int applicationID)
	{
		super(CSMessageType.REQUEST);
		this.applicationID = applicationID;
	}

	public int getApplicationID()
	{
		return this.applicationID;
	}
}

