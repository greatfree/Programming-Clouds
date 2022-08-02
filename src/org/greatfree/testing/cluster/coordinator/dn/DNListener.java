package org.greatfree.testing.cluster.coordinator.dn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.Prompts;
import org.greatfree.server.ServerListener;
import org.greatfree.testing.message.NodeKeyNotification;
import org.greatfree.util.ServerStatus;

/*
 * This is a coordinator listener that not only responds to cluster servers but also intends to connect and communicate with them. It assists the coordinator to interact with cluster servers in an eventing manner other than waiting for requests and then responding only. 11/28/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
// public class DNListener extends ServerListener implements Runnable
public class DNListener extends ServerListener
{
	/*
	 * Initialize the listener. 11/28/2014, Bing Li
	 */
	public DNListener(ServerSocket serverSocket, ThreadPool pool)
	{
		super(serverSocket, pool);
	}
	
	/*
	 * Shutdown the listener. 11/28/2014, Bing Li
	 */
	/*
	public void shutdown()
	{
		super.shutdown();
	}
	*/

	/*
	 * The task that must be executed concurrently. 11/28/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clusterSocket;
		DNIO serverIO;
		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from clients. 11/24/2014, Bing Li
		while (!super.isShutdown())
		{
			// Wait and accept a connecting from a possible client. 11/24/2014, Bing Li
			try
			{
				clusterSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 11/24/2014, Bing Li
				if (DNIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
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

				// If the upper limit of IOs is not reached, a DN server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the DN server IOs from a certain client could notify with each other with the same lock. Then, the upper limit of DN server IOs is under the control. 11/24/2014, Bing Li
//				serverIO = new DNIO(clusterSocket, super.getCollaborator(), ServerConfig.DN_PORT);
				serverIO = new DNIO(clusterSocket, super.getCollaborator());
				
				// Check whether a client to the IP and the port number of the remote end is existed in the client pool. If such a client does not exist, it is required to connect the remote end by the thread concurrently. Doing that concurrently is to speed up the rate of responding to the client. 09/17/2014, Bing Li
				if (!DNServerClientPool.COORDINATE().getPool().isClientExisted(serverIO.getRemoteServerKey()) || !DNServerClientPool.COORDINATE().getPool().isSourceExisted(serverIO.getIP(), ServerConfig.CLIENT_PORT))
				{
					DNServerClientPool.COORDINATE().getPool().send(serverIO.getIP(), ServerConfig.DN_PORT, new NodeKeyNotification(serverIO.getRemoteServerKey()));
				}

				// Add the new created server IO into the registry for further management. 08/22/2014, Bing Li
				DNIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created server IO concurrently to respond the client requests and notifications in an asynchronous manner. 08/22/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException e)
			{
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
