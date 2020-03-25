package org.greatfree.dip.threading.mrtc.slave;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.threading.mrtc.NodeIDs;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 09/22/2019, Bing Li
class StartSlave
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("MR Slave, " + NodeIDs.ID().getLocalName() + ", starting up ...");

		// Start the slave. 01/08/2020, Bing Li
		Slave.THREADING().start();

		/*
		 * Add tasks to the slave. 01/08/2020, Bing Li
		 */

		// Add the map task to the slave. 01/08/2020, Bing Li
		Slave.THREADING().addTask(new MapTask());
		// Add the reduce task to the slave. 01/08/2020, Bing Li
		Slave.THREADING().addTask(new ReduceTask());
		
		System.out.println("MR Slave, " + NodeIDs.ID().getLocalName() + ", started ...");
		
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
