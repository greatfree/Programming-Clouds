package com.greatfree.testing.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.greatfree.remote.IPPort;
import com.greatfree.remote.ServerListener;
import com.greatfree.testing.data.ServerConfig;

/*
 * The class is a server listener that not only responds to clients but also intends to connect and communicate with the client. The class assists the local server to interact with clients in an eventing manner other than waiting for requests and then responding only. 07/30/2014, Bing Li
 */

// Created: 07/30/2014, Bing Li
public class MyServerListener extends ServerListener implements Runnable
{
	// A thread to connect the remote client concurrently. 11/24/2014, Bing Li
	private ConnectClientThread connectThread;
	
	/*
	 * Initialize the listener. 08/22/2014, Bing Li
	 */
	public MyServerListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	{
		super(serverSocket, threadPoolSize, keepAliveTime);
	}

	/*
	 * Shutdown the listener. 11/24/2014, Bing Li
	 */
	public void shutdown()
	{
		// Dispose the connecting thread. 11/24/2014, Bing Li
		this.connectThread.dispose();
		// Shutdown the listener. 11/24/2014, Bing Li
		super.shutdown();
	}

	/*
	 * The task that must be executed concurrently. 08/22/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		MyServerIO serverIO;

		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from clients. 08/22/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible client. 08/22/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 08/22/2014, Bing Li
				if (MyServerIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 08/22/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				// If the upper limit of IOs is not reached, a server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the server IOs from a certain client could notify with each other with the same lock. Then, the upper limit of server IOs is under the control. 08/22/2014, Bing Li
				serverIO = new MyServerIO(clientSocket, super.getCollaborator());

				/*
				 * Since the listener servers need to form a peer-to-peer architecture, it is required to connect to the remote end. Thus, the local server can send messages without waiting for any requests from the remote end. 09/17/2014, Bing Li
				 */
				
				// Check whether a client to the IP and the port number of the remote end is existed in the client pool. If such a client does not exist, it is required to connect the remote end by the thread concurrently. Doing that concurrently is to speed up the rate of responding to the client. 09/17/2014, Bing Li
				if (!ClientPool.SERVER().getPool().isClientExisted(serverIO.getIP(), ServerConfig.REMOTE_SERVER_PORT))
				{
					// Since the client does not exist in the pool, input the IP address and the port number to the thread. 09/17/2014, Bing Li
					this.connectThread.enqueue(new IPPort(serverIO.getIP(), ServerConfig.REMOTE_SERVER_PORT));
					// Execute the thread to connect to the remote end. 09/17/2014, Bing Li
					super.execute(this.connectThread);
				}

				// Add the new created server IO into the registry for further management. 08/22/2014, Bing Li
				MyServerIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created server IO concurrently to respond the client requests and notifications in an asynchronous manner. 08/22/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
