package org.greatfree.concurrency.threading.message;

import org.greatfree.message.container.Notification;

// Created: 09/12/2019, Bing Li
public class ShutdownSlaveNotification extends Notification
{
	private static final long serialVersionUID = -1124835052555737353L;
	
	private long timeout;

	public ShutdownSlaveNotification(long timeout)
	{
		super(ATMMessageType.SHUTDOWN_SLAVE_NOTIFICATION);
		this.timeout = timeout;
	}

	public long getTimeout()
	{
		return this.timeout;
	}
}
