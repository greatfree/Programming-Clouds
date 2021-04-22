package org.greatfree.framework.player.tnti.slave;

import java.io.IOException;

import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.AddInterTask;
import org.greatfree.util.TerminateSignal;

/*
 * The "TTI/tti" represents Two-Node-Thread-Interaction. 10/07/2019, Bing Li
 */

// Created: 10/05/2019, Bing Li
class StartSlave
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("Player Slave starting up ...");
//		PlayerSystem.THREADING().startSlave(ThreadConfig.SLAVE, ThreadConfig.THREAD_PORT, ThreadConfig.MASTER);
		PlayerSystem.THREADING().startSlave(ThreadConfig.SLAVE, ThreadConfig.THREAD_PORT);
		System.out.println("Player Slave started ...");

		PlayerSystem.THREADING().addTask(new AddInterTask());

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
