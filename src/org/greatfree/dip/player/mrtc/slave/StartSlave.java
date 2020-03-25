package org.greatfree.dip.player.mrtc.slave;

import java.io.IOException;

import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 10/02/2019, Bing Li
class StartSlave
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("MR Slave, " + PlayerSystem.THREADING().getNickName() + ", starting up ...");

		MRSlaveTask task = new MRSlaveTask();
		PlayerSystem.THREADING().startSlave(ThreadConfig.THREAD_PORT, task);

		System.out.println("MR Slave, " + PlayerSystem.THREADING().getNickName() + ", started ...");

		PlayerSystem.THREADING().addTask(new MapTask());
		PlayerSystem.THREADING().addTask(new ReduceTask());

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
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
