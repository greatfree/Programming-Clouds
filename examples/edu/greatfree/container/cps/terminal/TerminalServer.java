package edu.greatfree.container.cps.terminal;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 12/31/2018, Bing Li
class TerminalServer
{
	private ServerContainer server;
	
	private TerminalServer()
	{
	}
	
	private static TerminalServer instance = new TerminalServer();
	
	public static TerminalServer CPS_CONTAINER()
	{
		if (instance == null)
		{
			instance = new TerminalServer();
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
