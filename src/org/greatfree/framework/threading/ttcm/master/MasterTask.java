package org.greatfree.framework.threading.ttcm.master;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.ThreadInfo;
import org.greatfree.framework.threading.message.PingNotification;
import org.greatfree.framework.threading.message.PongNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;

// Created: 09/13/2019, Bing Li
class MasterTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
		TaskStateNotification rst = (TaskStateNotification)notification;
		if (rst.getTaskKey().equals(TaskConfig.PING_TASK_KEY))
		{
			if (rst.isDone())
			{
				try
				{
					this.assignTask(ThreadInfo.ASYNC().getThreadBKey());
//					ActorMaster.CLIENT().wait(ThreadInfo.ASYNC().getThreadBKey());
					if (!Master.THREADING().isAlive(ThreadInfo.ASYNC().getThreadBKey()))
					{
						Master.THREADING().execute(ThreadInfo.ASYNC().getThreadBKey());
					}
				}
				catch (IOException | ClassNotFoundException | RemoteReadException e)
				{
					ServerStatus.FREE().printException(e);
				}
			}
		}
		else if (rst.getTaskKey().equals(TaskConfig.PONG_TASK_KEY))
		{
			if (rst.isDone())
			{
				try
				{
					this.assignTask(ThreadInfo.ASYNC().getThreadAKey());
					if (!Master.THREADING().isAlive(ThreadInfo.ASYNC().getThreadAKey()))
					{
						Master.THREADING().execute(ThreadInfo.ASYNC().getThreadAKey());
					}
				}
				catch (IOException | ClassNotFoundException | RemoteReadException e)
				{
					ServerStatus.FREE().printException(e);
				}
			}
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private void assignTask(String threadKey)
	{
		try
		{
			if (ThreadInfo.ASYNC().getThreadAKey().equals(threadKey))
			{
				Master.THREADING().assignTask(new PingNotification(ThreadInfo.ASYNC().getThreadAKey(), ThreadInfo.ASYNC().getThreadA() + " PINGS", 1000));
				System.out.println("MasterTask-assignTask(): PingNotification is sent to Thread A ...");
			}
			else
			{
				Master.THREADING().assignTask(new PongNotification(ThreadInfo.ASYNC().getThreadBKey(), ThreadInfo.ASYNC().getThreadB() + " PONGS", 1000));
				System.out.println("MasterTask-assignTask(): PongNotification is sent to Thread B ...");
			}
		}
		catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}

