package org.greatfree.testing.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.client.ServerListener;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.Prompts;
import org.greatfree.testing.message.NodeKeyNotification;
import org.greatfree.util.ServerStatus;

/*
 * The class is a server listener that not only responds to clients but also intends to connect and communicate with the client. The class assists the local server to interact with clients in an eventing manner other than waiting for requests and then responding only. 07/30/2014, Bing Li
 */

// Created: 07/30/2014, Bing Li
// public class CSServerListener extends ServerListener implements Runnable
public class CSServerListener extends ServerListener
{
	/*
	 * Initialize the listener. 08/22/2014, Bing Li
	 */
//	public MyServerListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	public CSServerListener(ServerSocket serverSocket, ThreadPool pool)
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
		// Shutdown the listener. 11/24/2014, Bing Li
		super.shutdown();
	}
	*/

	/*
	 * The task that must be executed concurrently. 08/22/2014, Bing Li
	 */
	public void run()
	{
		Socket clientSocket;
		CSServerIO serverIO;

		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from clients. 08/22/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible client. 08/22/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 08/22/2014, Bing Li
				if (CSServerIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
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
//				serverIO = new CSServerIO(clientSocket, super.getCollaborator(), ServerConfig.CLIENT_PORT);
				serverIO = new CSServerIO(clientSocket, super.getCollaborator());

				/*
				 * Since the listener servers need to form a peer-to-peer architecture, it is required to connect to the remote end. Thus, the local server can send messages without waiting for any requests from the remote end. 09/17/2014, Bing Li
				 */
				
				// Check whether a client to the IP and the port number of the remote end is existed in the client pool. If such a client does not exist, it is required to connect the remote end by the thread concurrently. Doing that concurrently is to speed up the rate of responding to the client. 09/17/2014, Bing Li
				if (!ClientPoolSingleton.SERVER().getPool().isClientExisted(serverIO.getRemoteServerKey()) || !ClientPoolSingleton.SERVER().getPool().isSourceExisted(serverIO.getIP(), ServerConfig.CLIENT_PORT))
				{
//					if (!ClientPool.SERVER().getPool().isClientExisted(serverIO.getIP(), ServerConfig.CLIENT_PORT))
//					{
						// Connect the remote peer. 07/06/2015, Bing Li
//						ClientPool.SERVER().getPool().send(serverIO.getIP(), ServerConfig.REMOTE_SERVER_PORT, new NodeKeyNotification(Tools.getKeyOfFreeClient(serverIO.getIP(), ServerConfig.REMOTE_SERVER_PORT)));
//						ClientPool.SERVER().getPool().send(serverIO.getIP(), ServerConfig.CLIENT_PORT, new NodeKeyNotification(Tools.getKeyOfFreeClient(serverIO.getIP(), ServerConfig.CLIENT_PORT)));
					ClientPoolSingleton.SERVER().getPool().send(serverIO.getIP(), ServerConfig.CLIENT_PORT, new NodeKeyNotification(serverIO.getRemoteServerKey()));
//					}
				}

//				System.out.println("1) MyServerListener: active count = " + super.getActiveCount());
				
				// Add the new created server IO into the registry for further management. 08/22/2014, Bing Li
				CSServerIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created server IO concurrently to respond the client requests and notifications in an asynchronous manner. 08/22/2014, Bing Li
				super.execute(serverIO);
//				System.out.println("2) MyServerListener: active count = " + super.getActiveCount());
			}
			catch (IOException e)
			{
//				e.printStackTrace();
//				System.out.println(Prompts.SOCKET_GOT_EXCEPTION);
				ServerStatus.FREE().printException(Prompts.SOCKET_GOT_EXCEPTION);
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
