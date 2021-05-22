package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.Request;

// Created: 01/13/2019, Bing Li
class ChildRequestThread extends NotificationQueue<Request>
{

	public ChildRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		Request request;
		MulticastResponse response;
//		Response subRootResponse;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.dequeue();
					/*
					 * I am implementing the root-based intercasting. It seems that the below lines are not necessary temporarily. 02/15/2019, Bing Li
					 */
					/*
					if (request.getRequestType() != MulticastMessageType.INTERCAST_REQUEST)
					{
						Child.CONTAINER().forward(request);
					}
					*/
//					Child.CONTAINER().forward(request);
					/*
					if (request.getRequestType() == MulticastMessageType.INTERCAST_REQUEST)
					{
						subRootResponse = ChildServiceProvider.CHILD().processIntercastRequest((IntercastRequest)request);
						Child.CONTAINER().notifyRoot(subRootResponse);
						this.disposeMessage(request);
						this.dispose(subRootResponse);
					}
					else
					{
						if (request.getRequestType() == MulticastMessageType.INTER_CHILDREN_REQUEST)
						{
							InterChildrenRequest icr = (InterChildrenRequest)request;
							response = ChildServiceProvider.CHILD().processRequest(icr);
							Child.CONTAINER().notifySubRoot(icr.getSubRootIP(), icr.getSubRootPort(), response);
						}
						else
						{
							response = ChildServiceProvider.CHILD().processRequest(request);
							Child.CONTAINER().notifyRoot(response);
						}
						this.disposeMessage(request);
						this.dispose(response);
					}
					*/
					if (request.getRequestType() == MulticastMessageType.INTER_CHILDREN_REQUEST)
					{
						InterChildrenRequest icr = (InterChildrenRequest)request;
						Child.CONTAINER().forward(icr);
						response = ChildServiceProvider.CHILD().processRequest(icr);
						/*
						 * It is not reasonable to notify the root when the child responds requests from the root since the notifications from the root also imposes workloads to the child. So I think the states of workloads should be implemented only on the application level. 09/11/2020, Bing Li
						 */
						/*
						if (Child.CONTAINER().isNormal())
						{
							Child.CONTAINER().notifySubRoot(icr.getSubRootIP(), icr.getSubRootPort(), response);
						}
						else
						{
							Child.CONTAINER().notifySubRoot(icr.getSubRootIP(), icr.getSubRootPort(), response, Child.CONTAINER().isBusy(), Child.CONTAINER().isIdle());
						}
						*/
						Child.CONTAINER().notifySubRoot(icr.getSubRootIP(), icr.getSubRootPort(), response);
					}
					else
					{
						Child.CONTAINER().forward(request);
						response = ChildServiceProvider.CHILD().processRequest(request);
						/*
						 * It is not reasonable to notify the root when the child responds requests from the root since the notifications from the root also imposes workloads to the child. So I think the states of workloads should be implemented only on the application level. 09/11/2020, Bing Li
						 */
						/*
						if (Child.CONTAINER().isNormal())
						{
							Child.CONTAINER().notifyRoot(response);
						}
						else
						{
							Child.CONTAINER().notifyRoot(response, Child.CONTAINER().isBusy(), Child.CONTAINER().isIdle());
						}
						*/
						Child.CONTAINER().notifyRoot(response);
					}
					this.disposeMessage(request);
					this.dispose(response);
				}
				catch (InterruptedException | IOException e)
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
