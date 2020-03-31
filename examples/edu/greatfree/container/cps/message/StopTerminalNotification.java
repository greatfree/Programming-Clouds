package edu.greatfree.container.cps.message;

import org.greatfree.message.container.Notification;

// Created: 12/31/2018, Bing Li
public class StopTerminalNotification extends Notification
{
	private static final long serialVersionUID = 8757297425536942468L;

	public StopTerminalNotification()
	{
		super(CPSApplicationID.STOP_TERMINAL_NOTIFICATION);
	}

}
