package edu.greatfree.tncs.client;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.data.ClientConfig;

import edu.greatfree.tncs.message.Merchandise;
import edu.greatfree.tncs.message.PostMerchandiseNotification;

// Created: 05/18/2019, Bing Li
class SyncECommerceEventer
{
	private SyncRemoteEventer<PostMerchandiseNotification> eventer;

	private FreeClientPool clientPool;

	private SyncECommerceEventer()
	{
	}

	private static SyncECommerceEventer instance = new SyncECommerceEventer();
	
	public static SyncECommerceEventer CS()
	{
		if (instance == null)
		{
			instance = new SyncECommerceEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws IOException
	{
		this.eventer.dispose();
		this.clientPool.dispose();
	}

	public void init()
	{
		this.clientPool = new FreeClientPool(ClientConfig.CLIENT_POOL_SIZE);
		this.clientPool.setIdleChecker(ClientConfig.CLIENT_IDLE_CHECK_DELAY, ClientConfig.CLIENT_IDLE_CHECK_PERIOD, ClientConfig.CLIENT_MAX_IDLE_TIME);

		this.eventer = new SyncRemoteEventer<PostMerchandiseNotification>(this.clientPool);
	}

	public void postMerchandise(Merchandise mcd) throws IOException, InterruptedException
	{
		this.eventer.notify(ECommerceClientConfig.ECOMMERCE_SERVER_ADDRESS, ECommerceClientConfig.ECOMMERCE_SERVER_PORT, new PostMerchandiseNotification(mcd));
	}
}
