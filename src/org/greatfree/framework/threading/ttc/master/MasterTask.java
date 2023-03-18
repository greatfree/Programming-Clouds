package org.greatfree.framework.threading.ttc.master;

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

// Created: 09/12/2019, Bing Li
class MasterTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.threading.ttc.master");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ATMMessageType.TASK_STATE_NOTIFICATION:
				log.info("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				TaskStateNotification rst = (TaskStateNotification)notification;
//				if (rst.getInstructType() == TaskMessageType.PRINT_TASK_NOTIFICATION)
				if (rst.getTaskKey().equals(TaskConfig.PRINT_TASK_KEY))
				{
					if (rst.isDone())
					{
						try
						{
							if (!rst.getThreadKey().equals(ThreadInfo.ASYNC().getThreadAKey()))
							{
//								Master.MASTER().wait(ThreadInfo.ASYNC().getThreadBKey());
								Master.MASTER().assignTask(ThreadInfo.ASYNC().getThreadAKey());
								if (!Master.MASTER().isAlive(ThreadInfo.ASYNC().getThreadAKey()))
								{
									Master.MASTER().execute(ThreadInfo.ASYNC().getThreadAKey());
								}
								/*
								else
								{
									Master.MASTER().notify(ThreadInfo.ASYNC().getThreadAKey());
								}
								*/
							}
							else
							{
//								Master.MASTER().wait(ThreadInfo.ASYNC().getThreadAKey());
								Master.MASTER().assignTask(ThreadInfo.ASYNC().getThreadBKey());
								if (!Master.MASTER().isAlive(ThreadInfo.ASYNC().getThreadBKey()))
								{
									Master.MASTER().execute(ThreadInfo.ASYNC().getThreadBKey());
								}
								/*
								else
								{
									Master.MASTER().notify(ThreadInfo.ASYNC().getThreadBKey());
								}
								*/
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

