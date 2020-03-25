package org.greatfree.concurrency.threading.message;

/*
 * The class should be abandoned. 09/18/2019, Bing Li
 * 
 * The class is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
 */
// Created: 09/12/2019, Bing Li
class SignalNotification extends InstructNotification
{
	private static final long serialVersionUID = 5757265600834171905L;

	public SignalNotification(String threadKey)
	{
//		super(threadKey, ThreadingMessageType.SIGNAL_NOTIFICATION);
		super(threadKey, 0);
	}

}
