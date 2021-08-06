package org.greatfree.concurrency.threading.message;

/*
 * The class should be abandoned. 09/18/2019, Bing Li
 * 
 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
 */

// Created: 09/11/2019, Bing Li
class TimeoutNotification extends ATMNotification
{
	private static final long serialVersionUID = -1050391014580722165L;
	
	private long time;

	public TimeoutNotification(String threadKey, long time)
	{
//		super(threadKey, ThreadingMessageType.TIMEOUT_NOTIFICATION);
		super(threadKey, 0);
		this.time = time;
	}
	
	public long getTime()
	{
		return this.time;
	}
}
