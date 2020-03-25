package org.greatfree.testing.pool;

import org.apache.commons.pool2.ObjectPool;
import org.greatfree.client.FreeClient;
import org.greatfree.message.ServerMessage;

// Created: 11/19/2018, Bing Li
public class NewFreeClientPool
{
	private ObjectPool<FreeClient> pool;

	public NewFreeClientPool(ObjectPool<FreeClient> pool)
	{
		this.pool = pool;
	}
	
	public void notify(ServerMessage msg) throws Exception
	{
		FreeClient client = null;
		try
		{
			client = this.pool.borrowObject();
			client.send(msg);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (client != null)
			{
				try
				{
					this.pool.returnObject(client);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public ServerMessage read(ServerMessage req) throws Exception
	{
		FreeClient client = null;
		try
		{
			client = this.pool.borrowObject();
			return client.sendWithResponse(req);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (client != null)
			{
				try
				{
					this.pool.returnObject(client);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
