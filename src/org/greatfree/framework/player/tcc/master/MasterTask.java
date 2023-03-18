package org.greatfree.framework.player.tcc.master;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.ATMTask;
import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.message.PrintTaskNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/30/2019, Bing Li
class MasterTask extends ATMTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.player.tcc.master");

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
					log.info("MasterTask-processNotification(): P2 is executed ... ");
					try
					{
						p2.notifyThreads(new PrintTaskNotification(p2.getThreadKey(), "B: Hello!", 1000));
					}
					catch (ClassNotFoundException | RemoteReadException | InterruptedException | ThreadAssignmentException | RemoteIPNotExistedException | IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (rst.getThreadKey().equals(p2.getThreadKey()))
				{
					log.info("MasterTask-processNotification(): P1 is executed ... ");
					try
					{
						p1.notifyThreads(new PrintTaskNotification(p1.getThreadKey(), "A: Hello!", 1000));
					}
					catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException | ThreadAssignmentException | RemoteIPNotExistedException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					log.info("MasterTask-processNotification(): the thread key does not make sense ... ");
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
