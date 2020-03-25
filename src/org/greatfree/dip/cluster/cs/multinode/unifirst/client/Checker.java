package org.greatfree.dip.cluster.cs.multinode.unifirst.client;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;

// Created: 02/16/2018, Bing Li
public class Checker implements Runnable
{

	@Override
	public void run()
	{
		try
		{
			ChatMaintainer.UNIFIRST().checkNewSessions();
			ChatMaintainer.UNIFIRST().checkNewChats();
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		
	}

}
