package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 03/30/2020, Bing Li
class DoubleServers
{
	private ServerContainer server1;
	private ServerContainer server2;

	private DoubleServers()
	{
	}
	
	private static DoubleServers instance = new DoubleServers();
	
	public static DoubleServers CS()
	{
		if (instance == null)
		{
			instance = new DoubleServers();
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
		this.server1.stop(timeout);
		this.server2.stop(timeout);
	}
	
	public void start(int port1, int port2, ServerTask task1, ServerTask task2) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server1 = new ServerContainer(port1, task1);
		this.server1.start();
		
		this.server2 = new ServerContainer(port2, task2);
		this.server2.start();
	}
}
