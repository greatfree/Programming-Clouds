package org.greatfree.framework.container.cs.twonode.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.twonode.server.AccountRegistry;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 12/18/2018, Bing Li
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
		TerminateSignal.SIGNAL().setTerminated();
		AccountRegistry.CS().dispose();
		this.server.stop(timeout);
	}
	
	public void start(int port, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new ServerContainer(port, task);
		this.server.start();
	}
	
//	public void start(int port, ServerTask task, String configXML) throws IOException, ClassNotFoundException, RemoteReadException
	public void start(ServerTask task, String configXML) throws IOException, ClassNotFoundException, RemoteReadException
	{
//		this.server = new ServerContainer(port, task, configXML);
		this.server = new ServerContainer(task, configXML);
		this.server.start();
	}
}



