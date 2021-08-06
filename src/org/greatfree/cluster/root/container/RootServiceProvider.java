package org.greatfree.cluster.root.container;

import org.greatfree.cluster.RootTask;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 01/27/2019, Bing Li
public class RootServiceProvider
{
	private RootTask task;
	
	private RootServiceProvider()
	{
	}
	
	private static RootServiceProvider instance = new RootServiceProvider();
	
	public static RootServiceProvider ROOT()
	{
		if (instance == null)
		{
			instance = new RootServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init(RootTask task)
	{
		this.task = task;
	}
	
	public void processNotification(ClusterNotification notification)
	{
		this.task.processNotification(notification);
	}

	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		return this.task.processRequest(request);
	}
	
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		return this.task.processChildRequest(request);
	}
}
