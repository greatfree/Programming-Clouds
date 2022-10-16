package org.greatfree.app.cps.front;

import java.io.IOException;

import org.greatfree.app.cps.message.MerchandiseRequest;
import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/14/2018, Bing Li
public class BusinessReader
{
	private BusinessReader()
	{
	}
	
	/*
	 * Initialize a singleton. 07/06/2018, Bing Li
	 */
	private static BusinessReader instance = new BusinessReader();
	
	public static BusinessReader RR()
	{
		if (instance == null)
		{
			instance = new BusinessReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init()
	{
		RemoteReader.REMOTE().init(ClientConfig.CLIENT_READER_POOL_SIZE);
	}

	/*
	 * Shutdown the remote reader. 11/23/2014, Bing Li
	 */
	public void shutdown() throws IOException, ClassNotFoundException
	{
		RemoteReader.REMOTE().shutdown();
	}
	
	public MerchandiseResponse query(String query) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return (MerchandiseResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new MerchandiseRequest(query)));
		return (MerchandiseResponse)(RemoteReader.REMOTE().read(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new MerchandiseRequest(query)));
	}
}
