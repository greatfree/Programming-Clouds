package edu.greatfree.threetier.front;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

import edu.greatfree.threetier.admin.AdminConfig;
import edu.greatfree.threetier.message.FrontRequest;
import edu.greatfree.threetier.message.FrontResponse;

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
		return (FrontResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), AdminConfig.COORDINATOR_ADDRESS, AdminConfig.COORDINATOR_PORT, new FrontRequest(query)));
	}

}
