package org.greatfree.cluster;

import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 01/27/2019, Bing Li
public interface RootTask
{
	public void processNotification(Notification notification);
	public Response processRequest(Request request);
	public ChildRootResponse processChildRequest(ChildRootRequest request);
}


