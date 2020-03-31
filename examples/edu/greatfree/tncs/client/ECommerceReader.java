package edu.greatfree.tncs.client;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

import edu.greatfree.tncs.message.MerchandiseRequest;
import edu.greatfree.tncs.message.MerchandiseResponse;

// Created: 05/01/2019, Bing Li
class ECommerceReader
{
	private ECommerceReader()
	{
	}
	
	private static ECommerceReader instance = new ECommerceReader();
	
	public static ECommerceReader CS()
	{
		if (instance == null)
		{
			instance = new ECommerceReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init()
	{
		RemoteReader.REMOTE().init(ECommerceClientConfig.CLIENT_READER_POOL_SIZE);
	}

	public void shutdown() throws IOException
	{
		RemoteReader.REMOTE().shutdown();
	}

	public MerchandiseResponse read(String model) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (MerchandiseResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ECommerceClientConfig.ECOMMERCE_SERVER_ADDRESS, ECommerceClientConfig.ECOMMERCE_SERVER_PORT, new MerchandiseRequest(model)));
	}
}
