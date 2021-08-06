package org.greatfree.framework.player.ct.master;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.ATMTask;
import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.message.PrintTaskNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/29/2019, Bing Li
class MasterTask extends ATMTask
{
	private Player p;
	
	public MasterTask()
	{
	}
	
	public void setPlayer(Player p)
	{
		this.p = p;
	}

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ATMMessageType.TASK_STATE_NOTIFICATION:
				System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				TaskStateNotification rst = (TaskStateNotification)notification;
				if (rst.getTaskKey().equals(TaskConfig.PRINT_TASK_KEY))
				{
					if (rst.isDone())
					{
						try
						{
							this.p.notifyThreads(new PrintTaskNotification(this.p.getThreadKey(), this.p.toString(), 1000));
						}
						catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException | ThreadAssignmentException e)
						{
							e.printStackTrace();
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
