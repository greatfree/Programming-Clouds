package edu.greatfree.cs.multinode.client;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

import edu.greatfree.cs.multinode.message.ChatMessageConfig;
import edu.greatfree.cs.multinode.message.ChatPartnerRequest;
import edu.greatfree.cs.multinode.message.ChatPartnerResponse;
import edu.greatfree.cs.multinode.message.ChatRegistryRequest;
import edu.greatfree.cs.multinode.message.ChatRegistryResponse;
import edu.greatfree.cs.multinode.message.PollNewChatsRequest;
import edu.greatfree.cs.multinode.message.PollNewChatsResponse;
import edu.greatfree.cs.multinode.message.PollNewSessionsRequest;
import edu.greatfree.cs.multinode.message.PollNewSessionsResponse;

/*
 * The class wraps the class, RemoteReader, to send requests to the remote server and wait until relevant responses are received. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
class ChatReader
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
	public void shutdown() throws IOException
	{
		/*
		try
		{
			RemoteReader.REMOTE().shutdown();
		}
		catch (IOException e)
		{
			ServerStatus.FREE().printException(e);
		}
		*/
		RemoteReader.REMOTE().shutdown();
	}
	
	/*
	 * Register the newly signed in user. 05/26/2017, Bing Li
	 */
	public ChatRegistryResponse registerChat(String userKey, String userName, String description)
	{
		try
		{
//			String userKey = Tools.getHash(userName);
			return (ChatRegistryResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatRegistryRequest(userKey, userName, description)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return ChatMessageConfig.NO_CS_CHAT_REGISTRY_RESPONSE;
	}

	/*
	 * Search one particular chatting partner. 05/26/2017, Bing Li
	 */
	public ChatPartnerResponse searchUser(String userKey)
	{
		try
		{
			return (ChatPartnerResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatPartnerRequest(userKey)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return ChatMessageConfig.NO_CS_CHAT_PARTNER_RESPONSE;
	}

	/*
	 * Poll the chatting server to check whether new chatting sessions are available. 05/26/2017, Bing Li
	 */
	public PollNewSessionsResponse checkNewSessions(String receiverKey, String receiverName)
	{
		try
		{
			return (PollNewSessionsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PollNewSessionsRequest(receiverKey, receiverName)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return ChatMessageConfig.NO_POLL_NEW_SESSIONS_RESPONSE;
	}

	/*
	 * Poll the chatting server to check whether new chatting messages are available. 05/26/2017, Bing Li
	 */
	public PollNewChatsResponse checkNewChats(String sessionKey, String receiverKey, String receiverName)
	{
		try
		{
			return (PollNewChatsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PollNewChatsRequest(sessionKey, receiverKey, receiverName)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return ChatMessageConfig.NO_POLL_NEW_CHATS_RESPONSE;
	}
}
