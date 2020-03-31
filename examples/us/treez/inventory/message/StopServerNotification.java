package us.treez.inventory.message;

import org.greatfree.message.container.Notification;

// Created: 02/05/2020, Bing Li
public class StopServerNotification extends Notification
{
	private static final long serialVersionUID = 2973894861139207025L;

	public StopServerNotification()
	{
		super(BusinessAppID.STOP_SERVER_NOTIFICATION);
	}

}
