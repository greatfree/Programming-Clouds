package org.greatfree.concurrency.threading.message;

/*
 * The class should be abandoned. 09/18/2019, Bing Li
 * 
 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
 */

// Created: 09/12/2019, Bing Li
class WaitNotification extends ATMNotification
{
	private static final long serialVersionUID = -4733372720567316434L;
	
	private long timeout;

	public WaitNotification(String threadKey)
	{
//		super(threadKey, ThreadingMessageType.WAIT_NOTIFICATION);
		super(threadKey, 0);
	}

	public WaitNotification(String threadKey, long timeout)
	{
//		super(threadKey, ThreadingMessageType.WAIT_NOTIFICATION);
		super(threadKey, 0);
		this.timeout = timeout;
	}
	
	public long getTimeout()
	{
		return this.timeout;
	}
}
