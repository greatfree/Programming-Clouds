package org.greatfree.server.container;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 12/18/2018, Bing Li
public interface ServerTask
{
	public void processNotification(Notification notification);
	public ServerMessage processRequest(Request request);
}


