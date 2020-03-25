package org.greatfree.concurrency.threading.message;

import org.greatfree.message.container.Request;

// Created: 09/10/2019, Bing Li
public class NotificationThreadRequest extends Request
{
	private static final long serialVersionUID = -7369972353171873867L;
	
	private int count;

	public NotificationThreadRequest(int count)
	{
		super(ThreadingMessageType.NOTIFICATION_THREAD_REQUEST);
		this.count = count;
	}
	
	public NotificationThreadRequest()
	{
		super(ThreadingMessageType.NOTIFICATION_THREAD_REQUEST);
		this.count = 1;
	}

	public int getCount()
	{
		return this.count;
	}
}

