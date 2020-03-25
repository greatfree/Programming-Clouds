package org.greatfree.demo.cps.message;

import org.greatfree.message.container.Notification;

// Created: 01/28/2019, Bing Li
public class OrderNotification extends Notification
{
	private static final long serialVersionUID = 1066342168858369770L;
	
	private String merchandise;
	private int count;

	public OrderNotification(String merchandise, int count)
	{
		super(ApplicationID.ORDER_NOTIFICATION);
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
