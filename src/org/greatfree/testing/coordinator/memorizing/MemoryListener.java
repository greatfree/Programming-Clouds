package org.greatfree.testing.coordinator.memorizing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.client.IPResource;
import org.greatfree.client.ServerListener;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;

/*
 * This is a coordinator listener that not only responds to memory servers but also intends to connect and communicate with them. It assists the coordinator to interact with memory servers in an eventing manner other than waiting for requests and then responding only. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
// public class MemoryListener extends ServerListener implements Runnable
public class MemoryListener extends ServerListener
{
	// A thread to connect the remote memory server concurrently. 11/28/2014, Bing Li
	private ConnectMemServerThread connectThread;

	/*
	 * Initialize the listener. 11/28/2014, Bing Li
	 */
//	public MemoryListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	public MemoryListener(ServerSocket serverSocket, ThreadPool pool)
	{
//		super(serverSocket, threadPoolSize, keepAliveTime);
		super(serverSocket, pool);
	}
	
	/*
	 * Shutdown the listener. 11/28/2014, Bing Li
	 */
	/*
	public void shutdown()
	{
		// Dispose the connecting thread. 11/28/2014, Bing Li
		this.connectThread.dispose();
		// Shutdown the listener. 11/28/2014, Bing Li
		super.dispose();
	}
	*/

	/*
	 * The task that must be executed concurrently. 11/28/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		MemoryIO serverIO;
		
		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from crawlers. 11/28/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible memory server. 11/28/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 11/28/2014, Bing Li
				if (MemoryIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 11/28/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				// If the upper limit of IOs is not reached, a memory server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the memory server IOs from a certain memory server could notify with each other with the same lock. Then, the upper limit of memory server IOs is under the control. 11/28/2014, Bing Li
//				serverIO = new MemoryIO(clientSocket, super.getCollaborator(), ServerConfig.MEMORY_SERVER_PORT);
				serverIO = new MemoryIO(clientSocket, super.getCollaborator());

				/*
				 * Since the listener servers need to form a peer-to-peer architecture, it is required to connect to the remote memory server. Thus, the local server can send messages without waiting for any requests from the remote end. 11/28/2014, Bing Li
				 */
				
				// Check whether a client to the IP and the port number of the remote memory server is existed in the client pool. If such a client does not exist, it is required to connect the remote memory server by the thread concurrently. Doing that concurrently is to speed up the rate of responding to the memory server. 11/28/2014, Bing Li
				if (!MemoryServerClientPool.COORDINATE().getPool().isSourceExisted(serverIO.getIP(), ServerConfig.MEMORY_SERVER_PORT))
				{
					// Since the client does not exist in the pool, input the IP address and the port number to the thread. 11/28/2014, Bing Li
					this.connectThread.enqueue(new IPResource(serverIO.getIP(), ServerConfig.MEMORY_SERVER_PORT));
					// Execute the thread to connect to the remote memory server. 11/28/2014, Bing Li
					super.execute(this.connectThread);
				}
				
				// Add the new created server IO into the registry for further management. 11/28/2014, Bing Li
				MemoryIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created memory IO concurrently to respond the memory servers' requests and notifications in an asynchronous manner. 11/28/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose() throws InterruptedException
	{
		// Dispose the connecting thread. 11/28/2014, Bing Li
		this.connectThread.dispose();
		// Shutdown the listener. 11/28/2014, Bing Li
		super.dispose();
	}

	/*
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}
	*/
}
