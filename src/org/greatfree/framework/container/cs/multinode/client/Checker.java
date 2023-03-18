package org.greatfree.framework.container.cs.multinode.client;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.twonode.client.ChatMaintainer;

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
		catch (ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
	}

}
