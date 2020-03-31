package edu.greatfree.container.p2p.registry;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 01/12/2019, Bing Li
class RegisterServer
{
	private ServerContainer server;

	private RegisterServer()
	{
	}
	
	private static RegisterServer instance = new RegisterServer();
	
	public static RegisterServer CS()
	{
		if (instance == null)
		{
			instance = new RegisterServer();
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
		Register.unregister();
		this.server.stop(timeout);
	}
	
	public void start(int port, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
		Register.register();
		this.server = new ServerContainer(port, task);
		this.server.start();
	}
	
	public void start(ServerTask task, String configXML) throws IOException, ClassNotFoundException, RemoteReadException
	{
		Register.register();
		this.server = new ServerContainer(task, configXML);
		this.server.start();
	}
}
