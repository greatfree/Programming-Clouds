package org.greatfree.dsf.threading.ct.master;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.concurrency.threading.message.ThreadingMessageType;
import org.greatfree.dsf.threading.TaskConfig;
import org.greatfree.dsf.threading.ThreadInfo;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;

/*
 * The case is not necessary since waiting is removed. The thread itself is managed in NotificationDispatcher. 09/18/2019, Bing Li
 * 
 * Case B: Wait for ever. 09/16/2019, Bing Li
 */

// Created: 09/16/2019, Bing Li
class MasterTaskB implements ServerTask
{
	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ThreadingMessageType.TASK_STATE_NOTIFICATION:
				System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				TaskStateNotification rst = (TaskStateNotification)notification;
				if (rst.getTaskKey().equals(TaskConfig.PRINT_TASK_KEY))
				{
					if (rst.isDone())
					{
						try
						{
//							Master.MASTER().wait(ThreadInfo.ASYNC().getThreadAKey());
							// The below task should never be processed. 09/16/2019, Bing Li
							Master.MASTER().assignTask(ThreadInfo.ASYNC().getThreadAKey());
						}
						catch (IOException | InterruptedException | ClassNotFoundException | RemoteReadException e)
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
