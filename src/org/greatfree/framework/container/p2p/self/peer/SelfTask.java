package org.greatfree.framework.container.p2p.self.peer;

import java.util.Calendar;

import org.greatfree.framework.container.p2p.message.SelfNotification;
import org.greatfree.framework.container.p2p.message.SelfRequest;
import org.greatfree.framework.container.p2p.message.SelfResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 10/03/2019, Bing Li
class SelfTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case SystemMessageType.SELF_NOTIFICATION:
				System.out.println("SELF_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SelfNotification sn = (SelfNotification)notification;
				System.out.println(sn.getMessage());
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case SystemMessageType.SELF_REQUEST:
				System.out.println("SELF_REQUEST received @" + Calendar.getInstance().getTime());
				SelfRequest sr = (SelfRequest)request;
				System.out.println(sr.getRequest());
				return new SelfResponse("answer");
		}
		return null;
	}
}
