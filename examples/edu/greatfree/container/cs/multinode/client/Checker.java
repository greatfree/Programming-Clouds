package edu.greatfree.container.cs.multinode.client;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;

// Created: 01/11/2019, Bing Li
class Checker implements Runnable
{

	@Override
	public void run()
	{
		try
		{
			ChatMaintainer.CS_CONTAINER().checkNewSessions();
			ChatMaintainer.CS_CONTAINER().checkNewChats();
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
	}

}
