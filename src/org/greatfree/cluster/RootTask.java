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
	public CollectedClusterResponse processRequest(ClusterRequest request);
	public ChildRootResponse processChildRequest(ChildRootRequest request);
}


