package org.greatfree.app.cps.message;

import org.greatfree.message.ServerMessage;

// Created: 08/14/2018, Bing Li
public class OrderNotification extends ServerMessage
{
	private static final long serialVersionUID = 3534899713313786051L;
	
	private String merchandise;
	private int count;

	public OrderNotification(String merchandise, int count)
	{
		super(BusinessMessageType.ORDER_NOTIFICATION);
		this.merchandise = merchandise;
		this.count = count;
	}

	public String getMerchandise()
	{
		return this.merchandise;
	}
	
	public int getCount()
	{
		return this.count;
	}
}
