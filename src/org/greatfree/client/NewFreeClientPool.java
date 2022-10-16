package org.greatfree.client;

import org.apache.commons.pool2.ObjectPool;
import org.greatfree.message.ServerMessage;

/*
 * The code intends to program a client pool upon apache's ObjectPool. It is not finished yet. 11/19/2018, Bing Li
 */

// Created: 11/19/2018, Bing Li
class NewFreeClientPool
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
