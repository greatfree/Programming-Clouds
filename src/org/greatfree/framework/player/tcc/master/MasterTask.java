package org.greatfree.framework.player.tcc.master;

import java.io.IOException;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerTask;
import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.message.PrintTaskNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/30/2019, Bing Li
class MasterTask extends PlayerTask
{
	private Player p1;
	private Player p2;

	public void setPlayers(Player p1, Player p2)
	{
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public void processNotification(Notification notification)
	{
		TaskStateNotification rst = (TaskStateNotification)notification;
		if (rst.getTaskKey().equals(TaskConfig.PRINT_TASK_KEY))
		{
			if (rst.isDone())
			{
				if (rst.getThreadKey().equals(p1.getThreadKey()))
				{
					System.out.println("MasterTask-processNotification(): P2 is executed ... ");
					try
					{
						p2.notifyThreads(new PrintTaskNotification(p2.getThreadKey(), "B: Hello!", 1000));
					}
					catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException | ThreadAssignmentException e)
					{
						e.printStackTrace();
					}
				}
				else if (rst.getThreadKey().equals(p2.getThreadKey()))
				{
					System.out.println("MasterTask-processNotification(): P1 is executed ... ");
					try
					{
						p1.notifyThreads(new PrintTaskNotification(p1.getThreadKey(), "A: Hello!", 1000));
					}
					catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException | ThreadAssignmentException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("MasterTask-processNotification(): the thread key does not make sense ... ");
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
