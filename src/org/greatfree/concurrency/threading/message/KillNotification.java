package org.greatfree.concurrency.threading.message;

// Created: 09/12/2019, Bing Li
public class KillNotification extends InstructNotification
{
	private static final long serialVersionUID = 3291382608698421753L;
	
	private long timeout;

	public KillNotification(String threadKey, long timeout)
	{
		super(threadKey, ThreadingMessageType.KILL_NOTIFICATION);
		this.timeout = timeout;
	}

	public long getTimeout()
	{
		return this.timeout;
	}
}

