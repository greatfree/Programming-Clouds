package org.greatfree.dsf.container.cs.multinode.server;

import java.io.IOException;

import org.greatfree.dsf.cs.multinode.server.PrivateChatSessions;
import org.greatfree.dsf.cs.twonode.server.AccountRegistry;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 12/31/2018, Bing Li
class ChatServer
{
	private ServerContainer server;
	
	private ChatServer()
	{
	}
	
	private static ChatServer instance = new ChatServer();
	
	public static ChatServer CS()
	{
		if (instance == null)
		{
			instance = new ChatServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		
		// Dispose the account registry. 04/30/2017, Bing Li
		AccountRegistry.CS().dispose();

		// Dispose the chatting session resources. 04/30/2017, Bing Li
		PrivateChatSessions.HUNGARY().dispose();

		this.server.stop(timeout);
	}

	public void start(int port, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the private chatting sessions. 04/23/2017, Bing Li
		PrivateChatSessions.HUNGARY().init();

		this.server = new ServerContainer(port, task);
		this.server.start();
	}
}
