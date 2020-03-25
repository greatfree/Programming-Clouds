package org.greatfree.cluster;

import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 10/01/2018, Bing Li
public interface ChildTask
{
	public void processNotification(Notification notification);
	public MulticastResponse processRequest(Request request);

	public InterChildrenNotification prepareNotification(IntercastNotification notification);
//	public InterChildrenRequest prepareRequest(IntercastRequest request);
//	public InterChildrenRequest prepareRequest(String subRootIP, int subRootPort, IntercastRequest request);
	public InterChildrenRequest prepareRequest(IntercastRequest request);
//	public void processDestinationNotification(InterChildrenNotification notification);
	
//	public Response processRequestAtRoot(Request request);
	public MulticastResponse processRequest(InterChildrenRequest request);
	public void processResponse(Response response);
}

