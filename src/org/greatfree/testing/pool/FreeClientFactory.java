package org.greatfree.testing.pool;

import java.io.IOException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.greatfree.client.FreeClient;

// Created: 11/19/2018, Bing Li
public class FreeClientFactory extends BasePooledObjectFactory<FreeClient>
{
	private final String ip;
	private final int port;
	
	public FreeClientFactory(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	@Override
	public FreeClient create() throws Exception
	{
		return new FreeClient(ip, port);
	}

	@Override
	public PooledObject<FreeClient> wrap(FreeClient client)
	{
		return new DefaultPooledObject<FreeClient>(client);
	}
	
	@Override
	public void passivateObject(PooledObject<FreeClient> pooledObject) throws IOException
	{
//		pooledObject.getObject().dispose();
	}
	
	@Override
	public void destroyObject(PooledObject<FreeClient> pooledObject) throws IOException
	{
		pooledObject.getObject().dispose();
	}
	

}
