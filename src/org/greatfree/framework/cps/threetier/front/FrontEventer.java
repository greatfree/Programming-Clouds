package org.greatfree.framework.cps.threetier.front;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.threetier.message.FrontNotification;
import org.greatfree.framework.p2p.RegistryConfig;

// Created: 07/06/2018, Bing Li
class FrontEventer
{
	// The notification of ChatNotification is sent to the chatting server in an asynchronous manner. 04/27/2017, Bing Li
	private SyncRemoteEventer<FrontNotification> fronter;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	private FrontEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Liaa
	 */
	private static FrontEventer instance = new FrontEventer();
	
	public static FrontEventer RE()
	{
		if (instance == null)
		{
			instance = new FrontEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws IOException, ClassNotFoundException
	{
		this.fronter.dispose();
		this.clientPool.dispose();
	}
	
	public void init()
	{
		 // Initialize the TCP client pool to send messages efficiently. 02/02/2016, Bing Li
		this.clientPool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 04/17/2017, Bing Li
		this.clientPool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);

		this.fronter = new SyncRemoteEventer<FrontNotification>(this.clientPool);
	}
	
	public void notify(String notification) throws IOException, InterruptedException
	{
		this.fronter.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new FrontNotification(notification));
	}
}
