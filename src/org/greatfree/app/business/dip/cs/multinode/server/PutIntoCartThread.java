package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PutIntoCartNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// CreatedL: 12/06/2017, Bing Li
public class PutIntoCartThread extends NotificationQueue<PutIntoCartNotification>
{

	public PutIntoCartThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PutIntoCartNotification notification;
		// Check whether the thread is shutdown or not. 12/05/2017, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the message queue of the thread is empty or not. 12/05/2017, Bing Li
			while (!this.isEmpty())
			{
				// Get the notification out from the message queue. 12/05/2017, Bing Li
				try
				{
					notification = this.dequeue();
					System.out.println("PutIntoCartThread: " + notification.getMerchandiseKey());
					// Create the response. 12/07/2017, Bing Li
					Businesses.putIntoCart(notification.getCustomerKey(), notification.getVendorKey(), notification.getMerchandiseKey(), notification.getCount());
					// Dispose the messages, including the request and the response. 12/05/2017, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new notifications might be received. If so, the thread can keep working. 02/15/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
