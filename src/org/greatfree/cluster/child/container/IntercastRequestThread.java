package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.IntercastRequestStream;
import org.greatfree.message.multicast.container.Response;

/*
 * Now I need to implement the root based intercasting. So the thread is not necessary temporarily. I will implement the children-based intercasing later. 02/15/2019, Bing Li 
 */

// Created: 01/26/2019, Bing Li
// class IntercastRequestThread extends RequestQueue<IntercastRequest, IntercastRequestStream, ServerMessage>
class IntercastRequestThread extends RequestQueue<IntercastRequest, IntercastRequestStream, Response>
{

	public IntercastRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		IntercastRequestStream request;
//		ServerMessage response = null;
		Response response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				/*
				 * Now I need to implement the root based intercasting. So the thread is not necessary temporarily. I will implement the children-based intercasing later. 02/15/2019, Bing Li 
				 */
				/*
				 * An intercast request is forwarded only once from the source child to the destination child. So if it is forwarded, it indicates that the local child is the destination. 02/10/2019, Bing Li
				 */
				/*
				if (!request.getMessage().isForwarded())
				{
					request.getMessage().setForwarded();
					response = Child.CONTAINER().readChild(request.getMessage());
				}
				else
				{
					response = ChildServiceProvider.CHILD().processRequest(request.getMessage());
				}
				this.respond(request.getOutStream(), request.getLock(), response);
				*/
				try
				{
					response = ChildServiceProvider.CHILD().processIntercastRequest(request.getMessage());
					ChildServiceProvider.CHILD().processIntercastResponse(response);
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (DistributedNodeFailedException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
