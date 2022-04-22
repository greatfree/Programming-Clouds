package org.greatfree.framework.cs.nio.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cs.nio.message.StopServerNotification;

/**
 * 
 * @author Bing Li
 * 
 * 02/08/2022
 *
 */
class StopServerNotificationThreadCreator implements NotificationQueueCreator<StopServerNotification, StopServerNotificationThread>
{

	@Override
	public StopServerNotificationThread createInstance(int taskSize)
	{
		return new StopServerNotificationThread(taskSize);
	}

}
