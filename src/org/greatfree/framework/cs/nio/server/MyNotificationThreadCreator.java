package org.greatfree.framework.cs.nio.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cs.nio.message.MyNotification;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class MyNotificationThreadCreator implements NotificationQueueCreator<MyNotification, MyNotificationThread>
{

	@Override
	public MyNotificationThread createInstance(int taskSize)
	{
		return new MyNotificationThread(taskSize);
	}

}
