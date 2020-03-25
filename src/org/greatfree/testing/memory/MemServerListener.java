package org.greatfree.testing.memory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.client.ServerListener;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;

/*
 * The class is a server listener that waits for the coordinator to distribute data. The design is the same as the general server listener. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
// public class MemServerListener extends ServerListener implements Runnable
public class MemServerListener extends ServerListener
{
	/*
	 * Initialize the listener. 11/27/2014, Bing Li
	 */
//	public MemServerListener(ServerSocket serverSocket)
	public MemServerListener(ServerSocket serverSocket, ThreadPool pool)
	{
//		super(serverSocket, MemConfig.MEMORY_LISTENER_THREAD_POOL_SIZE, MemConfig.MEMORY_LISTENER_THREAD_ALIVE_TIME);
		super(serverSocket, pool);
	}

	/*
	 * Waiting for connection concurrently. The connection request is invoked by a coordinator, which sends data storing/retrieving tasks and other management notifications. 11/27/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		MemoryIO serverIO;
		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from clients. 11/27/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible client residing on the coordinator. 11/27/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 11/27/2014, Bing Li
				if (MemoryIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 11/27/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				// If the upper limit of IOs is not reached, a server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the server IOs from a certain client could notify with each other with the same lock. Then, the upper limit of server IOs is under the control. 11/27/2014, Bing Li
//				serverIO = new MemoryIO(clientSocket, super.getCollaborator(), ServerConfig.COORDINATOR_PORT_FOR_MEMORY);
				serverIO = new MemoryIO(clientSocket, super.getCollaborator());
				// Add the new created server IO into the registry for further management. 11/27/2014, Bing Li
				MemoryIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created server IO concurrently to respond the client requests in an asynchronous manner. 11/27/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException e)
			{
				e.printStackTrace();
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
