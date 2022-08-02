package org.greatfree.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.Prompts;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.ServerStatus;

// Created: 04/20/2017, Bing Li
// public class CSListener<Dispatcher extends ServerDispatcher<ServerMessage>> extends ServerListener implements Runnable
class CSListener<Dispatcher extends ServerDispatcher<ServerMessage>> extends ServerListener
{
//	private final static Logger log = Logger.getLogger("org.greatfree.server");
	
//	private final int port;
	// Declare the server message producer, which is the important part of the server. Application developers can work on that directly through programming Dispatcher. 04/17/2017, Bing Li
	private ServerMessageProducer<Dispatcher> messageProducer;
	// The registry keeps all of the server IOs' instances. 04/19/2017, Bing Li
	private ServerIORegistry<CSServerIO<Dispatcher>> ioRegistry;
	private final int maxIOCount;

	/*
	 * The CSListener shares the thread pool of the dispatcher. Thus, the size of the pool is set upon the consideration of the count of the listeners. 06/03/2022, Bing Li
	 */
	public CSListener(ServerSocket serverSocket, ThreadPool pool, ServerMessageProducer<Dispatcher> messageProducer, ServerIORegistry<CSServerIO<Dispatcher>> ioRegistry, int maxIOCount)
	{
		super(serverSocket, pool);
		this.messageProducer = messageProducer;
		this.ioRegistry = ioRegistry;
		this.maxIOCount = maxIOCount;
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
		while (!super.isShutdown())
		{
//			System.out.println("\n===========================================");
//			log.info("Before a connection is accepted ... Code = " + this.hashCode());
			// Wait and accept a connecting from a possible client. 08/22/2014, Bing Li
			try
			{
				// Wait and accept a connecting from a possible client. 08/22/2014, Bing Li
				clientSocket = super.accept();
//				log.info("After a connection is accepted ... Code = " + this.hashCode() + "");
				// Check whether the connected server IOs exceed the upper limit. 08/22/2014, Bing Li
//				if (this.ioRegistry.getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				if (this.ioRegistry.getIOCount() >= this.maxIOCount)
				{
					// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 08/22/2014, Bing Li
					super.holdOn();
				}

//				log.info("A connection is established!");
//				System.out.println("===========================================\n");
				// If the upper limit of IOs is not reached, a server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the server IOs from a certain client could notify with each other with the same lock. Then, the upper limit of server IOs is under the control. 08/22/2014, Bing Li
//				serverIO = new DispatchingServerIO<Dispatcher>(clientSocket, super.getCollaborator(), this.port, this.messageProducer, this.ioRegistry);
				serverIO = new CSServerIO<Dispatcher>(clientSocket, super.getCollaborator(), this.messageProducer, this.ioRegistry);

				// Add the new created server IO into the registry for further management. 08/22/2014, Bing Li
				this.ioRegistry.addIO(serverIO);
				
//				System.out.println("CSListener-run(): IO count = " + this.ioRegistry.getIOCount());
				// Execute the new created server IO concurrently to respond the client requests and notifications in an asynchronous manner. 08/22/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException | InterruptedException e)
			{
				ServerStatus.FREE().printException(Prompts.SOCKET_GOT_EXCEPTION);
			}
		}
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	/*
	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void dispose(long timeout)
	{
		super.dispose();
	}
	*/
}
