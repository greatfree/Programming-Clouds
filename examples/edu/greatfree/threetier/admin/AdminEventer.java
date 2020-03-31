package edu.greatfree.threetier.admin;

import java.io.IOException;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.data.ClientConfig;

// Created: 05/07/2019, Bing Li
class AdminEventer
{
	private SyncRemoteEventer<ShutdownServerNotification> eventer;
	
	private FreeClientPool clientPool;

	private AdminEventer()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static AdminEventer instance = new AdminEventer();
	
	public static AdminEventer RE()
	{
		if (instance == null)
		{
			instance = new AdminEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws IOException
	{
		this.clientPool.dispose();
		this.eventer.dispose();
	}
	
	public void init()
	{
		this.clientPool = new FreeClientPool(ClientConfig.CLIENT_POOL_SIZE);
		this.clientPool.setIdleChecker(ClientConfig.CLIENT_IDLE_CHECK_DELAY, ClientConfig.CLIENT_IDLE_CHECK_PERIOD, ClientConfig.CLIENT_MAX_IDLE_TIME);
		this.eventer = new SyncRemoteEventer<ShutdownServerNotification>(this.clientPool);
	}
	
	public void shutdownCoordinator() throws IOException, InterruptedException
	{
		this.eventer.notify(AdminConfig.COORDINATOR_ADDRESS, AdminConfig.COORDINATOR_ADMIN_PORT, new ShutdownServerNotification());
	}
	
	public void shutdownTerminal() throws IOException, InterruptedException
	{
		this.eventer.notify(AdminConfig.TERMINAL_ADDRESS, AdminConfig.TERMINAL_ADMIN_PORT, new ShutdownServerNotification());
	}
	
}
