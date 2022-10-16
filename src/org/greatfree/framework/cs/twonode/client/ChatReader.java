package org.greatfree.framework.cs.twonode.client;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;

// Created: 05/13/2018, Bing Li
public class ChatReader
{
	private ChatReader()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static ChatReader instance = new ChatReader();
	
	public static ChatReader RR()
	{
		if (instance == null)
		{
			instance = new ChatReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Initialize the remote reader to send requests and receive responses from the remote server. 11/23/2014, Bing Li
	 */
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

	/*
	 * Register the newly signed in user. 05/26/2017, Bing Li
	 */
	public ChatRegistryResponse registerChat(String userKey, String userName, String description) throws ClassNotFoundException, RemoteReadException, IOException
	{
//			String userKey = Tools.getHash(userName);
		return (ChatRegistryResponse)(RemoteReader.REMOTE().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatRegistryRequest(userKey, userName, description)));
	}
}
