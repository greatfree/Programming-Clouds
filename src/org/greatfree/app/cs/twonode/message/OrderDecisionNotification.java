package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/27/2018, Bing Li
public class OrderDecisionNotification extends ServerMessage
{
	private static final long serialVersionUID = 4414046930280660805L;
	
	private boolean isDecided;

	public OrderDecisionNotification(boolean isDecided)
	{
		super(BusinessMessageType.ORDER_DECISION_NOTIFICATION);
		this.isDecided = isDecided;
	}

	public boolean isDecided()
	{
		return this.isDecided;
	}
}
