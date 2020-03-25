package org.greatfree.app.container.cs.multinode.business.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 01/24/2019, Bing Li
class BusinessServer
{
	private ServerContainer server;
	
	private BusinessServer()
	{
	}
	
	private static BusinessServer instance = new BusinessServer();
	
	public static BusinessServer CS()
	{
		if (instance == null)
		{
			instance = new BusinessServer();
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
		this.server.stop(timeout);
	}
	
	public void start(int port, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new ServerContainer(port, task);
		this.server.start();
	}
}
