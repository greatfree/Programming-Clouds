package org.greatfree.framework.player.tr.master;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.concurrency.threading.ATMTask;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/30/2019, Bing Li
class MasterTask extends ATMTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.player.tr.master");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ATMMessageType.TASK_STATE_NOTIFICATION:
				log.info("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
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
