package org.greatfree.client;

import java.io.IOException;

import org.greatfree.data.ClientConfig;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 05/02/2022
 * 
 * To keep the consistency with RemoteReader, the SyncRemoteEventer is updated to be as a singleton as well. 05/02/2022
 *
 */
public final class SingleSyncRemoteEventer
{
	private SyncRemoteEventer<ServerMessage> eventer;
	private FreeClientPool clientPool;

	private SingleSyncRemoteEventer()
	{
	}

	private static SingleSyncRemoteEventer instance = new SingleSyncRemoteEventer();
	
	public static SingleSyncRemoteEventer REMOTE()
	{
		if (instance == null)
		{
			instance = new SingleSyncRemoteEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws IOException
	{
		this.eventer.dispose();
		this.clientPool.dispose();
	}
	
	public void init(int poolSize)
	{
		this.clientPool = new FreeClientPool(poolSize);
		this.clientPool.setIdleChecker(ClientConfig.CLIENT_IDLE_CHECK_DELAY, ClientConfig.CLIENT_IDLE_CHECK_PERIOD, ClientConfig.CLIENT_MAX_IDLE_TIME);

		this.eventer = new SyncRemoteEventer<ServerMessage>(this.clientPool);
	}
	
	public void notify(String ip, int port, ServerMessage message) throws IOException, InterruptedException
	{
		this.eventer.notify(ip, port, message);
	}
}
