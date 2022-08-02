package org.greatfree.testing.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.Prompts;
import org.greatfree.server.ServerListener;
import org.greatfree.util.ServerStatus;

/*
 * The listener holds on waiting for connections from the remote end. 07/30/2014, Bing Li 
 */

// Created: 11/07/2014, Bing Li
//public class ClientListener extends ServerListener implements Runnable
public class ClientListener extends ServerListener
{
	/*
	 * Initialize the listener. The instance of ServerSocket is used to wait for connections with remote ends. 11/07/2014, Bing Li
	 */
//	public ClientListener(ServerSocket serverSocket)
	public ClientListener(ServerSocket serverSocket, ThreadPool pool)
	{
//		super(serverSocket, ClientConfig.CLIENT_LISTENER_THREAD_POOL_SIZE, ClientConfig.CLIENT_LISTENER_THREAD_ALIVE_TIME);
		super(serverSocket, pool);
	}

	/*
	 * The connection waiting is executed asynchronously. 11/07/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Declare the instance of client socket. 11/07/2014, Bing Li
		Socket clientSocket;
		// Declare the instance of ClientServerIO. 11/07/2014, Bing Li
		ClientServerIO serverIO;
		// The listener should be always keeping alive unless the client is shutdown. 11/07/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait for remote connections. 11/07/2014, Bing Li
				clientSocket = super.accept();
				// Check if the connections reach the upper limit. If the total count of connections exceeds the maximum count, it is required to wait until a connection is disconnected. It attempts to protect the resources from being used up. 11/07/2014, Bing Li
				if (ClientServerIORegistry.REGISTRY().getIOCount() >= ClientConfig.SERVER_IO_POOL_SIZE)
				{
					try
					{
						// Wait until a connection is disconnected. 11/07/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				// Once if a connection is constructed, initialize the instance of ClientServerIO with the instance of Socket. 11/07/2014, Bing Li
//				serverIO = new ClientServerIO(clientSocket, super.getCollaborator(), ServerConfig.CLIENT_PORT);
				serverIO = new ClientServerIO(clientSocket, super.getCollaborator());
				// Add the instance of ClientServerIO to the registry for management. 11/07/2014, Bing Li
				ClientServerIORegistry.REGISTRY().addIO(serverIO);
				// Start the instance of ClientServerIO asynchronously to wait for messages for further processing. 11/07/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException e)
			{
//				e.printStackTrace();
				ServerStatus.FREE().printException(Prompts.LISTENER_CLOSED);
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
