package org.greatfree.cluster;

import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 01/27/2019, Bing Li
public interface RootTask
{
	public void processNotification(ClusterNotification notification);
	
	/*
	 * The method is rarely used. But it is reasonable to keep it since it is possible that a client sends a request to the root only. 09/29/2022, Bing Li
	 */
	public CollectedClusterResponse processRequest(ClusterRequest request);
	public ChildRootResponse processChildRequest(ChildRootRequest request);
}


