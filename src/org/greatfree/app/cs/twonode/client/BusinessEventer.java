package org.greatfree.app.cs.twonode.client;

import java.io.IOException;

import org.greatfree.app.cs.twonode.message.Merchandise;
import org.greatfree.app.cs.twonode.message.OrderDecisionNotification;
import org.greatfree.app.cs.twonode.message.PostMerchandiseNotification;
import org.greatfree.app.cs.twonode.message.ShutdownBusinessServerNotification;
import org.greatfree.chat.ChatConfig;
import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.framework.p2p.RegistryConfig;

// Created: 07/27/2018, Bing Li
public class BusinessEventer
{
	private SyncRemoteEventer<OrderDecisionNotification> orderEventer;
	private SyncRemoteEventer<ShutdownBusinessServerNotification> shutdownEventer;
	
	private SyncRemoteEventer<PostMerchandiseNotification> postEventer;
	
	private FreeClientPool clientPool;

	private BusinessEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static BusinessEventer instance = new BusinessEventer();
	
	public static BusinessEventer NOTIFY()
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

	public void dispose(long timeout) throws InterruptedException, IOException
	{
		this.clientPool.dispose();
		this.orderEventer.dispose();
		this.postEventer.dispose();
		this.shutdownEventer.dispose();
	}
	
	public void init()
	{
		this.clientPool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		this.clientPool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);

		this.orderEventer = new SyncRemoteEventer<OrderDecisionNotification>(this.clientPool);
		this.shutdownEventer = new SyncRemoteEventer<ShutdownBusinessServerNotification>(this.clientPool);
		this.postEventer = new SyncRemoteEventer<PostMerchandiseNotification>(this.clientPool);
	}

	public void notifyOrder(boolean isDecided) throws IOException, InterruptedException
	{
		this.orderEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new OrderDecisionNotification(isDecided));
	}

	public void shutdown() throws IOException, InterruptedException
	{
		this.shutdownEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_ADMIN_PORT, new ShutdownBusinessServerNotification());
	}
	
	public void post(String vendor, String merchandise, int inStockQuantity) throws IOException, InterruptedException
	{
		this.postEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PostMerchandiseNotification(vendor, new Merchandise(merchandise, inStockQuantity)));
	}
}
