package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

// Created: 10/01/2018, Bing Li
public class JoinNotification extends ServerMessage
{
	private static final long serialVersionUID = 4689698379560269786L;
	
	private String childID;

	public JoinNotification(String childID)
	{
		super(ClusterMessageType.JOIN_NOTIFICATION);
		this.childID = childID;
	}

	public String getChildID()
	{
		return this.childID;
	}
}
