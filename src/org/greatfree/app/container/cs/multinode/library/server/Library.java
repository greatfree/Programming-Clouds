package org.greatfree.app.container.cs.multinode.library.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 12/19/2018, Bing Li
public class Library
{
	private ServerContainer server;

	private Library()
	{
	}

	private static Library instance = new Library();

	public static Library CS()
	{
		if (instance == null)
		{
			instance = new Library();
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

//	public void start(int port, ServerTask task, String configXML) throws IOException, ClassNotFoundException, RemoteReadException
	public void start(ServerTask task, String configXML) throws IOException, ClassNotFoundException, RemoteReadException
	{
//		this.server = new ServerContainer(port, task, configXML);
		this.server = new ServerContainer(task, configXML);
		this.server.start();
	}
}