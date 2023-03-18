package org.greatfree.framework.cluster.cs.multinode.unifirst.client;

import org.greatfree.exceptions.RemoteIPNotExistedException;
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
		catch (ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
		
	}

}
