package edu.greatfree.container.cps.message;

import org.greatfree.message.container.Notification;

// Created: 12/31/2018, Bing Li
public class FrontNotification extends Notification
{
	private static final long serialVersionUID = -1210827940748591139L;
	
	private String notification;

	public FrontNotification(String notification)
	{
		super(CPSApplicationID.FRONT_NOTIFICATION);
		this.notification = notification;
	}

	public String getNotification()
	{
		return this.notification;
	}
}
