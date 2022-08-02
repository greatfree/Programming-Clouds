package org.greatfree.testing.cluster.dn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.server.ServerListener;
import org.greatfree.util.ServerStatus;

/*
 * The class is a server listener that waits for the coordinator to distribute messages. The design is the same as the general server listener. 07/30/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
// public class DNListener extends ServerListener implements Runnable
public class DNListener extends ServerListener
{
	/*
	 * Initialize the listener. 11/23/2014, Bing Li
	 */
	public DNListener(ServerSocket serverSocket, ThreadPool pool)
	{
		super(serverSocket, pool);
	}

	/*
	 * Waiting for connection concurrently. The connection request is invoked by a coordinator, which sends crawling tasks and other management notifications. 11/23/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		DNIO serverIO;

		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from clients. 11/23/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible client residing on the coordinator. 11/23/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 11/23/2014, Bing Li
				if (DNIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 11/23/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				// If the upper limit of IOs is not reached, a server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the server IOs from a certain client could notify with each other with the same lock. Then, the upper limit of server IOs is under the control. 11/23/2014, Bing Li
//				serverIO = new DNIO(clientSocket, super.getCollaborator(), ServerConfig.COORDINATOR_DN_PORT);
				serverIO = new DNIO(clientSocket, super.getCollaborator());
				// Add the new created server IO into the registry for further management. 11/23/2014, Bing Li
				DNIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created server IO concurrently to respond the client requests in an asynchronous manner. 11/23/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
	}

	/*
	@Override
	public void dispose() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
