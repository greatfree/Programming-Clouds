package org.greatfree.testing.coordinator.searching;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.client.IPResource;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.server.ServerListener;
import org.greatfree.testing.coordinator.memorizing.MemoryServerClientPool;

/*
 * This is a coordinator listener that not only responds to search clients. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
// public class SearchListener extends ServerListener implements Runnable
public class SearchListener extends ServerListener
{
	// A thread to connect the search client concurrently. 11/29/2014, Bing Li
	private ConnectSearcherThread connectThread;

	/*
	 * Initialize the listener. 11/29/2014, Bing Li
	 */
//	public SearchListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	public SearchListener(ServerSocket serverSocket, ThreadPool pool)
	{
//		super(serverSocket, threadPoolSize, keepAliveTime);
		super(serverSocket, pool);
	}
	
	/*
	 * Shutdown the listener. 11/29/2014, Bing Li
	 */
	/*
	public void shutdown()
	{
		// Dispose the connecting thread. 11/29/2014, Bing Li
		this.connectThread.dispose();
		// Shutdown the listener. 11/29/2014, Bing Li
		super.shutdown();
	}
	*/

	/*
	 * The task that must be executed concurrently. 11/29/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		SearchIO serverIO;

		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from crawlers. 11/29/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible memory server. 11/29/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 11/29/2014, Bing Li
				if (SearchIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 11/29/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				// If the upper limit of IOs is not reached, a search server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the search server IOs from a certain search client could notify with each other with the same lock. Then, the upper limit of search server IOs is under the control. 11/29/2014, Bing Li
//				serverIO = new SearchIO(clientSocket, super.getCollaborator(), ServerConfig.SEARCH_CLIENT_PORT);
				serverIO = new SearchIO(clientSocket, super.getCollaborator());

				/*
				 * Since the listener servers need to form a peer-to-peer architecture, it is required to connect to the remote search client. Thus, the local server can send messages without waiting for any requests from the searcher. 11/29/2014, Bing Li
				 */
				
				// Check whether a client to the IP and the port number of the remote search clients is existed in the client pool. If such a client does not exist, it is required to connect the remote search client by the thread concurrently. Doing that concurrently is to speed up the rate of responding to the search client. 11/29/2014, Bing Li
				if (!MemoryServerClientPool.COORDINATE().getPool().isSourceExisted(serverIO.getIP(), ServerConfig.SEARCH_CLIENT_PORT))
				{
					// Since the client does not exist in the pool, input the IP address and the port number to the thread. 11/29/2014, Bing Li
					this.connectThread.enqueue(new IPResource(serverIO.getIP(), ServerConfig.SEARCH_CLIENT_PORT));
					// Execute the thread to connect to the remote search client. 11/29/2014, Bing Li
					super.execute(this.connectThread);
				}
				
				// Add the new created server IO into the registry for further management. 11/29/2014, Bing Li
				SearchIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created search IO concurrently to respond the search clients' requests and notifications in an asynchronous manner. 11/29/2014, Bing Li
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
		// Dispose the connecting thread. 11/29/2014, Bing Li
		this.connectThread.dispose();
		// Shutdown the listener. 11/29/2014, Bing Li
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
