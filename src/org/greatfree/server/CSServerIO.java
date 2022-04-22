package org.greatfree.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import org.greatfree.client.ServerIO;
import org.greatfree.client.ServerIORegistry;
import org.greatfree.concurrency.Sync;
import org.greatfree.exceptions.Prompts;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.ServerStatus;

/*
 * The class extends ServerIO and adds the message producer and the server dispatcher. It is used to define a general server as a server idiom. 04/19/2017, Bing Li
 */

// Created: 04/19/2017, Bing Li
class CSServerIO<Dispatcher extends ServerDispatcher<ServerMessage>> extends ServerIO
{
	private final static Logger log = Logger.getLogger("org.greatfree.server");
	
	// Declare the server message producer, which is the important part of the server. Application developers can work on that directly through programming Dispatcher. 04/17/2017, Bing Li
	private ServerMessageProducer<Dispatcher> messageProducer;
	// The registry keeps all of the server IOs' instances. 04/19/2017, Bing Li
	private ServerIORegistry<CSServerIO<Dispatcher>> ioRegistry;

	/*
	 * The message producer is programmed by developers such that they can define their own upper level applications. 04/19/2017, Bing Li
	 * 
	 * Initialize the server IO. The socket is the connection between the client and the server. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/23/2014, Bing Li
	 */
//	public DispatchingServerIO(Socket clientSocket, Sync collaborator, int remoteServerPort, ServerMessageProducer<Dispatcher> messageProducer, ServerIORegistry<DispatchingServerIO<Dispatcher>> ioRegistry)
	public CSServerIO(Socket clientSocket, Sync collaborator, ServerMessageProducer<Dispatcher> messageProducer, ServerIORegistry<CSServerIO<Dispatcher>> ioRegistry)
	{
//		super(clientSocket, collaborator, remoteServerPort);
		super(clientSocket, collaborator);
		this.messageProducer = messageProducer;
		this.ioRegistry = ioRegistry;
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 08/22/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{		
			try
			{
//				log.info("Waiting for messages ... ");
				// Wait and read messages from a client. 08/22/2014, Bing Li
				message = (ServerMessage)super.read();
				
//				log.info("Message received: type = " + message.getType());
				
				this.messageProducer.produceMessage(new MessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (ClassNotFoundException | IOException e)
			{
//				log.info("Waiting for messages got exceptions ... ");
				log.info(e.toString());
				// If the remote node is not shutdown, it indicates that the remote node disconnects one connection and the exception is raised. Then, relevant management tasks need to be accomplished. 02/06/2016, Bing Li 
//				if (!ServerStatus.FREE().isServerDown(AdminConfig.CLIENT_ID))
//				{
				try
				{
					// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
					this.ioRegistry.removeIO(this);
					// Remote the client from the pool. 02/20/2016, Bing Li
//						ClientPool.SERVER().getPool().removeClient(this.getRemoteServerKey());
				}
				catch (IOException | InterruptedException ex)
				{
					ServerStatus.FREE().printException(Prompts.SERVER_IO_GOT_EXCEPTION);
				}
//				}
				// Exist the loop such that the instance is collected. 02/06/2016, Bing Li
				return;
			}
		}
	}
}
