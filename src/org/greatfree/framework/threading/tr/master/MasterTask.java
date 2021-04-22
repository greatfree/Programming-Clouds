package org.greatfree.framework.threading.tr.master;

import java.util.Calendar;

import org.greatfree.concurrency.threading.PlayerTask;
import org.greatfree.concurrency.threading.message.ThreadingMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/28/2019, Bing Li
class MasterTask extends PlayerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ThreadingMessageType.TASK_STATE_NOTIFICATION:
				System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				return;
		}
		super.processNotify(notification);
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		return super.processRead(request);
	}

}

