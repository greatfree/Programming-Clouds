package org.greatfree.client;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.ServerStatus;

/*
 * This is a thread derived from NotificationObjectQueue. It keeps working until no objects are available in the queue. The thread keeps alive unless it is shutdown by a manager outside. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
class Eventer<Notification extends ServerMessage> extends NotificationObjectQueue<IPNotification<Notification>>
{
	// An instance of FreeClientPool through which the notification can be sent. 11/20/2014, Bing Li
	private FreeClientPool pool;
	// The time to be waited when no objects are available in the queue. 11/20/2014, Bing Li
	private long waitTime;

	/*
	 * Initialize the eventer. It must notice that the primary component of the eventer, FreeClientPool, comes from outside. It represents that the eventer shares the pool with others. 11/20/2014, Bing Li
	 */
	public Eventer(int queueSize, long waitTime, FreeClientPool pool)
	{
		super(queueSize);
		this.pool = pool;
		this.waitTime = waitTime;
	}

	/*
	 * The task must be executed concurrently. 11/20/2014, Bing Li
	 */
	public void run()
	{
		// The object to be dequeued from the queue. 11/20/2014, Bing Li
		IPNotification<Notification> notification;
		// Check whether the eventer is set to be shutdown. 11/20/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the queue is empty. 11/20/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the object from the queue. 11/20/2014, Bing Li
					notification = this.dequeue();
					try
					{
						// Since the object contains the IP/port and the notification, it is convenient to send the notification by the FreeClientPool. 11/20/2014, Bing Li
						this.pool.send(notification.getIPPort(), notification.getNotification());
					}
					catch (IOException e)
					{
						ServerStatus.FREE().printException(e);
					}
					// Dispose the notification after it is sent out. 11/20/2014, Bing Li
					this.disposeObject(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for some time when no objects are available in the queue. 11/20/2014, Bing Li
				this.holdOn(this.waitTime);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
	}
}
