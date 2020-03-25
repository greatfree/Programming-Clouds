package org.greatfree.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.client.ServerIORegistry;
import org.greatfree.client.ServerListener;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.Prompts;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.message.NodeKeyNotification;
import org.greatfree.util.ServerStatus;

/*
 * To support developers' programming upper level applications, the listener is upgraded by adding the programmed message producer and the IO registry. 04/19/2017, Bing Li
 * 
 * The class is a server listener that not only responds to clients but also intends to connect and communicate with the client. The class assists the local server to interact with clients in an eventing manner other than waiting for requests and then responding only. 07/30/2014, Bing Li
 */

// Created: 04/19/2017, Bing Li
// public class P2PListener<Dispatcher extends ServerDispatcher<ServerMessage>> extends ServerListener implements Runnable
public class P2PListener<Dispatcher extends ServerDispatcher<ServerMessage>> extends ServerListener
{
	private final int port;
	// Declare the server message producer, which is the important part of the server. Application developers can work on that directly through programming Dispatcher. 04/17/2017, Bing Li
	private ServerMessageProducer<Dispatcher> messageProducer;
	// The registry keeps all of the server IOs' instances. 04/19/2017, Bing Li
	private ServerIORegistry<CSServerIO<Dispatcher>> ioRegistry;

	public P2PListener(ServerSocket serverSocket, int port, ThreadPool pool, ServerMessageProducer<Dispatcher> messageProducer, ServerIORegistry<CSServerIO<Dispatcher>> ioRegistry)
	{
		super(serverSocket, pool);
		this.port = port;
		this.messageProducer = messageProducer;
		this.ioRegistry = ioRegistry;
	}

	/*
	 * The task that must be executed concurrently. 08/22/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		CSServerIO<Dispatcher> serverIO;
		
		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from clients. 08/22/2014, Bing Li
		while (!this.isShutdown())
		{
			// Wait and accept a connecting from a possible client. 08/22/2014, Bing Li
			try
			{
				// Wait and accept a connecting from a possible client. 08/22/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 08/22/2014, Bing Li
				if (this.ioRegistry.getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 08/22/2014, Bing Li
					super.holdOn();
				}
				
				System.out.println("The peer consumes " + this.ioRegistry.getIOCount() + " IOServers");
				
				// If the upper limit of IOs is not reached, a server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the server IOs from a certain client could notify with each other with the same lock. Then, the upper limit of server IOs is under the control. 08/22/2014, Bing Li
//				serverIO = new DispatchingServerIO<Dispatcher>(clientSocket, super.getCollaborator(), ServerConfig.CLIENT_PORT, this.messageProducer, this.ioRegistry);
				serverIO = new CSServerIO<Dispatcher>(clientSocket, super.getCollaborator(), this.messageProducer, this.ioRegistry);

				// Check whether a client to the IP and the port number of the remote end is existed in the client pool. If such a client does not exist, it is required to connect the remote end by the thread concurrently. Doing that concurrently is to speed up the rate of responding to the client. 09/17/2014, Bing Li
//				if (!ClientPool.SERVER().getPool().isClientExisted(serverIO.getRemoteServerKey()) || !ClientPool.SERVER().getPool().isSourceExisted(serverIO.getIP(), ServerConfig.CLIENT_PORT))
				if (!ClientPoolSingleton.SERVER().getPool().isClientExisted(serverIO.getRemoteServerKey()))
				{
					ClientPoolSingleton.SERVER().getPool().send(serverIO.getIP(), this.port, new NodeKeyNotification(serverIO.getRemoteServerKey()));
				}

				// Add the new created server IO into the registry for further management. 08/22/2014, Bing Li
				this.ioRegistry.addIO(serverIO);
				// Execute the new created server IO concurrently to respond the client requests and notifications in an asynchronous manner. 08/22/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException | InterruptedException e)
			{
				ServerStatus.FREE().printException(Prompts.SOCKET_GOT_EXCEPTION);
			}
			
		}
		
	}

	/*
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void dispose(long timeout)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
