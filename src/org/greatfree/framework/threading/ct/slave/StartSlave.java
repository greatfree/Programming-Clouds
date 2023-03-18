package org.greatfree.framework.threading.ct.slave;

import java.io.IOException;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

/*
 * The CT/ct represents "Controlling Threads". 09/16/2019, Bing Li
 */

// Created: 09/10/2019, Bing Li
class StartSlave
{
	public static void main(String[] args) throws RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Thread slave starting up ...");
		
		try
		{
			Slave.SLAVE().start(ThreadConfig.SLAVE, ThreadConfig.MASTER, new SlaveTask());
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}
		catch (IOException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("Thread slave started ...");

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

