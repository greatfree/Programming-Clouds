package org.greatfree.demo.message;

import org.greatfree.message.container.Notification;

// Created: 01/24/2019, Bing Li
public class PostMerchandiseNotification extends Notification
{
	private static final long serialVersionUID = -8414012815928398431L;
	
	private Merchandise m;

	public PostMerchandiseNotification(Merchandise m)
	{
		super(ApplicationID.POST_MERCHANDISE_NOTIFICATION);
		this.m = m;
	}

	public Merchandise getMerchandise()
	{
		return this.m;
	}
}
