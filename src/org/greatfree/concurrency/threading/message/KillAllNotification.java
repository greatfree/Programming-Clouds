package org.greatfree.concurrency.threading.message;

import org.greatfree.util.UtilConfig;

// Created: 09/14/2019, Bing Li
public class KillAllNotification extends ATMNotification
{
	private static final long serialVersionUID = -5670379451715925477L;
	
	private long timeout;

	public KillAllNotification(long timeout)
	{
		super(UtilConfig.EMPTY_STRING, ATMMessageType.KILL_ALL_NOTIFICATION);
		this.timeout = timeout;
	}

	public long getTimeout()
	{
		return this.timeout;
	}
}
