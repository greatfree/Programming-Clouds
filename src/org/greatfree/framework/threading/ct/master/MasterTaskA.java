package org.greatfree.framework.threading.ct.master;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.ThreadInfo;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;

/*
 * Case A. 09/16/2019, Bing Li
 */

// Created: 09/16/2019, Bing Li
class MasterTaskA implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.threading.ct.master");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ATMMessageType.TASK_STATE_NOTIFICATION:
				log.info("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				TaskStateNotification rst = (TaskStateNotification)notification;
				if (rst.getTaskKey().equals(TaskConfig.PRINT_TASK_KEY))
				{
					if (rst.isDone())
					{
						try
						{
//							Master.MASTER().wait(ThreadInfo.ASYNC().getThreadAKey());
							Master.MASTER().assignTask(ThreadInfo.ASYNC().getThreadAKey());
//							Thread.sleep(3000);
//							Thread.sleep(8000);
							if (!Master.MASTER().isAlive(ThreadInfo.ASYNC().getThreadAKey()))
							{
								log.info("MasterTask-processNotification(): The thread is NOT Alive");
								Master.MASTER().execute(ThreadInfo.ASYNC().getThreadAKey());
							}
							else
							{
								log.info("MasterTask-processNotification(): The thread is Alive");
//								Master.MASTER().signal(ThreadInfo.ASYNC().getThreadAKey());
							}
						}
						catch (IOException | InterruptedException | ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
						{
							ServerStatus.FREE().printException(e);
						}
					}
				}
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
