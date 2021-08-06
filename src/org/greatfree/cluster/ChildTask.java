package org.greatfree.cluster;

import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 10/01/2018, Bing Li
public interface ChildTask
{
	public void processNotification(ClusterNotification notification);
	public MulticastResponse processRequest(ClusterRequest request);

	public InterChildrenNotification prepareNotification(IntercastNotification notification);
//	public InterChildrenRequest prepareRequest(IntercastRequest request);
//	public InterChildrenRequest prepareRequest(String subRootIP, int subRootPort, IntercastRequest request);
	public InterChildrenRequest prepareRequest(IntercastRequest request);
//	public void processDestinationNotification(InterChildrenNotification notification);
	
//	public Response processRequestAtRoot(Request request);
	public MulticastResponse processRequest(InterChildrenRequest request);
	public void processResponse(CollectedClusterResponse response);
}

