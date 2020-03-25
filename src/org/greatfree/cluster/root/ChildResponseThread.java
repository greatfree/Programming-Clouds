package org.greatfree.cluster.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.container.ChildResponse;

// Created: 10/02/2018, Bing Li
class ChildResponseThread extends NotificationQueue<ChildResponse>
{

	public ChildResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ChildResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					response = this.getNotification();
					ClusterRoot.CLUSTER().getRP().saveResponse(response.getResponse());
					this.disposeMessage(response);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
			}

			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
