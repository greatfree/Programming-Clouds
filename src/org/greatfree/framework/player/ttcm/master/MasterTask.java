package org.greatfree.framework.player.ttcm.master;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.ATMTask;
import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.message.PingNotification;
import org.greatfree.framework.threading.message.PongNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/30/2019, Bing Li
class MasterTask extends ATMTask
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
		System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
		TaskStateNotification rst = (TaskStateNotification)notification;
		if (rst.getTaskKey().equals(TaskConfig.PING_TASK_KEY))
		{
			if (rst.isDone())
			{
				try
				{
					p2.notifyThreads(new PongNotification(p2.getThreadKey(), "T2 PONGS", 1000));
				}
				catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException | ThreadAssignmentException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if (rst.getTaskKey().equals(TaskConfig.PONG_TASK_KEY))
		{
			if (rst.isDone())
			{
				try
				{
					p1.notifyThreads(new PingNotification(p1.getThreadKey(), "T1 PINGS", 1000));
				}
				catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException | ThreadAssignmentException e)
				{
					e.printStackTrace();
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
