package org.greatfree.dsf.threading.ttc.dt.master;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.dsf.threading.TaskConfig;
import org.greatfree.dsf.threading.ThreadInfo;
import org.greatfree.dsf.threading.message.PrintTaskNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;

// Created: 09/14/2019, Bing Li
 class MasterTask implements ServerTask
{
	@Override
	public void processNotification(Notification notification)
	{
		System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
		TaskStateNotification rst = (TaskStateNotification)notification;
		if (rst.getTaskKey().equals(TaskConfig.PRINT_TASK_KEY))
		{
			if (rst.isDone())
			{
				try
				{
					if (!rst.getThreadKey().equals(ThreadInfo.ASYNC().getThreadAKey()))
					{
						System.out.println("1) MasterTask-processNotification(): ...");
//								ActorMaster.CLIENT().wait(ThreadInfo.ASYNC().getThreadBKey());
//						ActorMaster.THREADING().assignTask(new PrintTaskNotification(ThreadInfo.ASYNC().getThreadBKey(), "I am " + ThreadInfo.ASYNC().getThreadName(ThreadInfo.ASYNC().getThreadBKey()), ThreadConfig.TIMEOUT));
//						Master.THREADING().assignTask(new PrintTaskNotification(ThreadInfo.ASYNC().getThreadBKey(), "I am " + ThreadInfo.ASYNC().getThreadName(ThreadInfo.ASYNC().getThreadBKey()), ThreadConfig.TIMEOUT));
						if (!Master.THREADING().isAlive(ThreadInfo.ASYNC().getThreadAKey()))
						{
							System.out.println("1) MasterTask-processNotification(): T1 is NOT alive");
//							ActorMaster.THREADING().execute(ThreadInfo.ASYNC().getThreadAKey());
							Master.THREADING().execute(ThreadInfo.ASYNC().getThreadAKey());
						}
						Master.THREADING().assignTask(new PrintTaskNotification(ThreadInfo.ASYNC().getThreadAKey(), "I am " + ThreadInfo.ASYNC().getThreadName(ThreadInfo.ASYNC().getThreadAKey()), ThreadConfig.TIMEOUT));
//						if (!ActorMaster.THREADING().isAlive(ThreadInfo.ASYNC().getThreadAKey()))
						/*
						else
						{
							System.out.println("1) MasterTask-processNotification(): T1 is alive");
							ActorMaster.CLIENT().signal(ThreadInfo.ASYNC().getThreadAKey());
						}
						*/
					}
					else
					{
						System.out.println("2) MasterTask-processNotification(): ...");
//								ActorMaster.CLIENT().wait(ThreadInfo.ASYNC().getThreadAKey());
//						ActorMaster.THREADING().assignTask(new PrintTaskNotification(ThreadInfo.ASYNC().getThreadAKey(), "I am " + ThreadInfo.ASYNC().getThreadName(ThreadInfo.ASYNC().getThreadAKey()), ThreadConfig.TIMEOUT));
//						Master.THREADING().assignTask(new PrintTaskNotification(ThreadInfo.ASYNC().getThreadAKey(), "I am " + ThreadInfo.ASYNC().getThreadName(ThreadInfo.ASYNC().getThreadAKey()), ThreadConfig.TIMEOUT));
						if (!Master.THREADING().isAlive(ThreadInfo.ASYNC().getThreadBKey()))
						{
							System.out.println("2) MasterTask-processNotification(): T2 is NOT alive");
//							ActorMaster.THREADING().execute(ThreadInfo.ASYNC().getThreadBKey());
							Master.THREADING().execute(ThreadInfo.ASYNC().getThreadBKey());
						}
						Master.THREADING().assignTask(new PrintTaskNotification(ThreadInfo.ASYNC().getThreadBKey(), "I am " + ThreadInfo.ASYNC().getThreadName(ThreadInfo.ASYNC().getThreadBKey()), ThreadConfig.TIMEOUT));

//						if (!ActorMaster.THREADING().isAlive(ThreadInfo.ASYNC().getThreadBKey()))
						/*
						else
						{
							System.out.println("2) MasterTask-processNotification(): T2 is alive");
							ActorMaster.CLIENT().signal(ThreadInfo.ASYNC().getThreadBKey());
						}
						*/
					}
				}
				catch (IOException | InterruptedException | ClassNotFoundException | RemoteReadException e)
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
}
