package org.greatfree.framework.player.ct.slave;

import java.io.IOException;

import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.threading.PrintTask;
import org.greatfree.util.TerminateSignal;

// Created: 09/29/2019, Bing Li
class StartSlave
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Player Slave starting up ...");

//		PlayerSystem.THREADING().startSlave(ThreadConfig.SLAVE, ThreadConfig.THREAD_PORT, ThreadConfig.MASTER);
		PlayerSystem.THREADING().startSlave(ThreadConfig.SLAVE, ThreadConfig.THREAD_PORT);

		System.out.println("Player Slave started ...");

		PlayerSystem.THREADING().addTask(new PrintTask());

		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
