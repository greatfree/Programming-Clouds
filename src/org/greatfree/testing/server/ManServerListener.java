package org.greatfree.testing.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.client.ServerListener;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.Prompts;
import org.greatfree.util.ServerStatus;

/*
 * The listener waits for the notification from Administrator to stop itself. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
// public class ManServerListener extends ServerListener implements Runnable
public class ManServerListener extends ServerListener
{
	/*
	 * Initialize the listener. 01/20/2016, Bing Li
	 */
//	public ManServerListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	public ManServerListener(ServerSocket serverSocket, ThreadPool pool)
	{
		super(serverSocket, pool);
	}
	
	/*
	 * Shutdown the listener. 01/20/2016, Bing Li
	 */
	/*
	public void shutdown()
	{
		// Shutdown the listener. 01/20/2016, Bing Li
		super.shutdown();
	}
	*/

	/*
	 * The task that must be executed concurrently. 01/20/2016, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		ManIO manIO;
		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from clients. 01/20/2016, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible client. 01/20/2016, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 01/20/2016, Bing Li
				if (ManIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 01/20/2016, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				// If the upper limit of IOs is not reached, a man server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the man server IOs from a certain crawler could notify with each other with the same lock. Then, the upper limit of man server IOs is under the control. 01/20/2016, Bing Li
//				manIO = new ManIO(clientSocket, super.getCollaborator(), ServerConfig.COORDINATOR_PORT_FOR_ADMIN);
				manIO = new ManIO(clientSocket, super.getCollaborator());
				// Add the new created server IO into the registry for further management. 01/20/2016, Bing Li
				ManIORegistry.REGISTRY().addIO(manIO);
				// Execute the new created man IO concurrently to respond the crawlers requests and notifications in an asynchronous manner. 01/20/2016, Bing Li
				super.execute(manIO);
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
