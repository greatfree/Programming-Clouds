package org.greatfree.testing.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.greatfree.admin.AdminConfig;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerIO;
import org.greatfree.util.ServerStatus;

/*
 * This is an implementation of ServerIO to receive and even respond remote ends. In this case of the client, it only receives feedbacks (notifications) from a remote server to set up the local ObjectInputStream. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class ClientServerIO extends ServerIO
{
	// Initialize the server IO. 11/07/2014, Bing Li
//	public ClientServerIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	public ClientServerIO(Socket clientSocket, Sync collaborator)
	{
//		super(clientSocket, collaborator, remoteServerPort);
		super(clientSocket, collaborator);
	}

	/*
	 * A concurrent running thread to receive and respond the received messages asynchronously. 11/07/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			try
			{
				// Wait and read messages from a client. 11/07/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/07/2014, Bing Li
				ClientServerMessageProducer.CLIENT().produceMessage(new MessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (SocketException e)
			{
				// If the remote node is not shutdown, it indicates that the remote node disconnects one connection and the exception is raised. Then, relevant management tasks need to be accomplished. 02/06/2016, Bing Li 
				if (!ServerStatus.FREE().isServerDown(AdminConfig.SERVER_ID))
				{
//					e.printStackTrace();
					try
					{
						// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
						ClientServerIORegistry.REGISTRY().removeIO(this);
					}
					catch (IOException | InterruptedException e1)
					{
						e1.printStackTrace();
						// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//						System.out.println(Prompts.SERVER_IO_SHUTDOWN);
					}
					// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//					System.out.println(Prompts.SERVER_IO_SHUTDOWN);
				}
				// Exist the loop such that the instance is collected. 02/06/2016, Bing Li
				return;
			}
			catch (IOException e)
			{
				// If the remote node is not shutdown, it indicates that the remote node disconnects one connection and the exception is raised. Then, relevant management tasks need to be accomplished. 02/06/2016, Bing Li 
				if (!ServerStatus.FREE().isServerDown(AdminConfig.SERVER_ID))
				{
//					e.printStackTrace();
					try
					{
						// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
						ClientServerIORegistry.REGISTRY().removeIO(this);
					}
					catch (IOException | InterruptedException e1)
					{
						e1.printStackTrace();
						// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//						System.out.println(Prompts.SERVER_IO_SHUTDOWN);
					}
					// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//					System.out.println(Prompts.SERVER_IO_SHUTDOWN);
				}
				// Exist the loop such that the instance is collected. 02/06/2016, Bing Li
				return;
			}
			catch (ClassNotFoundException e)
			{
				// If the remote node is not shutdown, it indicates that the remote node disconnects one connection and the exception is raised. Then, relevant management tasks need to be accomplished. 02/06/2016, Bing Li 
				if (!ServerStatus.FREE().isServerDown(AdminConfig.SERVER_ID))
				{
//					e.printStackTrace();
					try
					{
						// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
						ClientServerIORegistry.REGISTRY().removeIO(this);
					}
					catch (IOException | InterruptedException e1)
					{
						e1.printStackTrace();
						// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//						System.out.println(Prompts.SERVER_IO_SHUTDOWN);
					}
					// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//					System.out.println(Prompts.SERVER_IO_SHUTDOWN);
				}
				// Exist the loop such that the instance is collected. 02/06/2016, Bing Li
				return;
			}
		}
	}
}
