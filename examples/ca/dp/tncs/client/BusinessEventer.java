package ca.dp.tncs.client;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.dip.p2p.RegistryConfig;

import ca.dp.tncs.message.PlaceOrderNotification;
import ca.dp.tncs.message.TNCSConfig;

// Created: 02/22/2020, Bing Li
class BusinessEventer
{
	private SyncRemoteEventer<PlaceOrderNotification> client;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	private BusinessEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static BusinessEventer instance = new BusinessEventer();
	
	public static BusinessEventer RE()
	{
		if (instance == null)
		{
			instance = new BusinessEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws IOException
	{
		this.client.dispose();
		this.clientPool.dispose();
	}
	
	public void init()
	{
		 // Initialize the TCP client pool to send messages efficiently. 02/02/2016, Bing Li
		this.clientPool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 04/17/2017, Bing Li
		this.clientPool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);
		this.client = new SyncRemoteEventer<PlaceOrderNotification>(this.clientPool);
	}
	
	public void notify(String cn, String bn, int bc, float payment) throws IOException, InterruptedException
	{
		this.client.notify(TNCSConfig.SERVER_IP, TNCSConfig.SERVER_PORT, new PlaceOrderNotification(cn, bn, bc, payment));
	}
}
