package org.greatfree.dsf.cps.threetier.front;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.threetier.message.FrontRequest;
import org.greatfree.dsf.cps.threetier.message.FrontResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

// Created: 07/06/2018, Bing Li
class FrontReader
{
	private FrontReader()
	{
	}
	
	/*
	 * Initialize a singleton. 07/06/2018, Bing Li
	 */
	private static FrontReader instance = new FrontReader();
	
	public static FrontReader RR()
	{
		if (instance == null)
		{
			instance = new FrontReader();
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
	public void shutdown() throws IOException
	{
		RemoteReader.REMOTE().shutdown();
	}

	public FrontResponse query(String query) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (FrontResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new FrontRequest(query)));
	}

}
