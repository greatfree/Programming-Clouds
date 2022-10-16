package org.greatfree.app.cs.twonode.client;

import java.io.IOException;

import org.greatfree.app.cs.twonode.message.MerchandisePollRequest;
import org.greatfree.app.cs.twonode.message.MerchandisePollResponse;
import org.greatfree.app.cs.twonode.message.MerchandiseRequest;
import org.greatfree.app.cs.twonode.message.MerchandiseResponse;
import org.greatfree.chat.ChatConfig;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 07/27/2018, Bing Li
public class BusinessReader
{
	private BusinessReader()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
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
//		return (MerchandiseResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new MerchandiseRequest(query)));
		return (MerchandiseResponse)(RemoteReader.REMOTE().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new MerchandiseRequest(query)));
	}
	
	public MerchandisePollResponse poll(String merchandise, int quantity) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return (MerchandisePollResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new MerchandisePollRequest(merchandise, quantity)));
		return (MerchandisePollResponse)(RemoteReader.REMOTE().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new MerchandisePollRequest(merchandise, quantity)));
	}
	
}
