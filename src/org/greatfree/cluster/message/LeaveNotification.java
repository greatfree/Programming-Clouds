package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

// Created: 10/01/2018, Bing Li
public class LeaveNotification extends ServerMessage
{
	private static final long serialVersionUID = 2238537070987864335L;
	
	private String childID;

	public LeaveNotification(String childID)
	{
		super(ClusterMessageType.LEAVE_NOTIFICATION);
		this.childID = childID;
	}

	public String getChildID()
	{
		return this.childID;
	}
}
