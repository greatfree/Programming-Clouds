package edu.greatfree.container.cps.message;

import org.greatfree.message.container.Notification;

// Created: 12/31/2018, Bing Li
public class StopCoordinatorNotification extends Notification
{
	private static final long serialVersionUID = 1519286598052806337L;

	public StopCoordinatorNotification()
	{
		super(CPSApplicationID.STOP_COORDINATOR_NOTIFICATION);
	}

}
