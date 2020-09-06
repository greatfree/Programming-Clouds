package org.greatfree.dsf.player.mrtc.slave;

import java.util.Calendar;

import org.greatfree.concurrency.threading.PlayerTask;
import org.greatfree.concurrency.threading.message.ThreadingMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 10/02/2019, Bing Li
class MRSlaveTask extends PlayerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			// Since each slave probably plays the role of the master in one map/reduce round, it must prepare for receiving task states from its slaves. 04/03/2020, Bing Li 
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
