package org.greatfree.testing.coordinator.crawling;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.client.IPResource;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.server.ServerListener;

/*
 * This is a coordinator listener that not only responds to crawlers but also intends to connect and communicate with them. It assists the coordinator to interact with crawlers in an eventing manner other than waiting for requests and then responding only. 11/24/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
// public class CrawlListener extends ServerListener implements Runnable
public class CrawlListener extends ServerListener
{
	// A thread to connect the remote crawler concurrently. 11/24/2014, Bing Li
	private ConnectCrawlServerThread connectThread;

	/*
	 * Initialize the listener. 11/24/2014, Bing Li
	 */
//	public CrawlListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	public CrawlListener(ServerSocket serverSocket, ThreadPool pool)
	{
//		super(serverSocket, threadPoolSize, keepAliveTime);
		super(serverSocket, pool);
	}
	
	/*
	 * Shutdown the listener. 11/24/2014, Bing Li
	 */
	/*
	public void shutdown()
	{
		// Dispose the connecting thread. 11/24/2014, Bing Li
		this.connectThread.dispose();
		// Shutdown the listener. 11/24/2014, Bing Li
		super.dispose();
	}
	*/

	/*
	 * The task that must be executed concurrently. 11/24/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		CrawlIO serverIO;

		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from crawlers. 11/24/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible crawler. 11/24/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 11/24/2014, Bing Li
				if (CrawlIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 11/24/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				// If the upper limit of IOs is not reached, a crawling server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the crawling server IOs from a certain crawler could notify with each other with the same lock. Then, the upper limit of crawling server IOs is under the control. 11/24/2014, Bing Li
//				serverIO = new CrawlIO(clientSocket, super.getCollaborator(), ServerConfig.COORDINATOR_PORT_FOR_CRAWLER);
				serverIO = new CrawlIO(clientSocket, super.getCollaborator());

				/*
				 * Since the listener servers need to form a peer-to-peer architecture, it is required to connect to the remote crawling server. Thus, the local server can send messages without waiting for any requests from the remote end. 11/24/2014, Bing Li
				 */
				
				// Check whether a client to the IP and the port number of the remote crawling server is existed in the client pool. If such a client does not exist, it is required to connect the remote crawler by the thread concurrently. Doing that concurrently is to speed up the rate of responding to the crawling server. 11/24/2014, Bing Li
				if (!CrawlServerClientPool.COORDINATE().getPool().isSourceExisted(serverIO.getIP(), ServerConfig.CRAWL_SERVER_PORT))
				{
					// Since the client does not exist in the pool, input the IP address and the port number to the thread. 11/25/2014, Bing Li
					this.connectThread.enqueue(new IPResource(serverIO.getIP(), ServerConfig.CRAWL_SERVER_PORT));
					// Execute the thread to connect to the remote crawler. 11/25/2014, Bing Li
					super.execute(this.connectThread);
				}
				
				// Add the new created server IO into the registry for further management. 11/25/2014, Bing Li
				CrawlIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created crawling IO concurrently to respond the crawlers' requests and notifications in an asynchronous manner. 11/25/2014, Bing Li
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
		// Dispose the connecting thread. 11/24/2014, Bing Li
		this.connectThread.dispose();
		// Shutdown the listener. 11/24/2014, Bing Li
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
