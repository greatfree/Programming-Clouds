package edu.greatfree.container.cs.multinode.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cs.multinode.ChatConfig;

// Created: 01/07/2018, Bing Li, in the airplane to Xi'An
class StartChatServer
{

	public static void main(String[] args)
	{
		System.out.println("Chatting server starting up ...");
		
		try
		{
			ChatServer.CS().start(ChatConfig.CHAT_SERVER_PORT, new ChatServerTask());
		}
		catch (IOException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("Chatting server started ...");

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ChatConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
