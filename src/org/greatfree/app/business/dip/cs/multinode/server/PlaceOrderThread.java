package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PlaceOrderNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Created: 12/07/2017, Bing Li
public class PlaceOrderThread extends NotificationQueue<PlaceOrderNotification>
{

	public PlaceOrderThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PlaceOrderNotification notification;
		// Check whether the thread is shutdown or not. 12/05/2017, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the message queue of the thread is empty or not. 12/05/2017, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Get the notification out from the message queue. 12/05/2017, Bing Li
					notification = this.dequeue();
					// Place the order. 12/15/2017, Bing Li
					Businesses.placeOrder(notification.getVendorKey(), notification.getCustomerKey());
					// Dispose the notification. 12/09/2017, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 02/15/2016, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
