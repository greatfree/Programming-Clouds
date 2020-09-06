package org.greatfree.chat.client.business.dip;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.ChatMessageConfig;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.dsf.cs.multinode.message.ChatPartnerRequest;
import org.greatfree.dsf.cs.multinode.message.ChatPartnerResponse;
import org.greatfree.dsf.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.dsf.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.dsf.cs.multinode.message.PollNewChatsRequest;
import org.greatfree.dsf.cs.multinode.message.PollNewChatsResponse;
import org.greatfree.dsf.cs.multinode.message.PollNewSessionsRequest;
import org.greatfree.dsf.cs.multinode.message.PollNewSessionsResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;
import org.greatfree.util.ServerStatus;

/*
 * The class wraps the class, RemoteReader, to send requests to the remote server and wait until relevant responses are received. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
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
	public void shutdown()
	{
		try
		{
			RemoteReader.REMOTE().shutdown();
		}
		catch (IOException e)
		{
			ServerStatus.FREE().printException(e);
		}
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
	public PollNewSessionsResponse checkNewSessions(String receiverKey, String username)
	{
		try
		{
			return (PollNewSessionsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PollNewSessionsRequest(receiverKey, username)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			ServerStatus.FREE().setShutdown();
//			e.printStackTrace();
			ServerStatus.FREE().printException(e);
		}
		return ChatMessageConfig.NO_POLL_NEW_SESSIONS_RESPONSE;
	}

	/*
	 * Poll the chatting server to check whether new chatting messages are available. 05/26/2017, Bing Li
	 */
	public PollNewChatsResponse checkNewChats(String sessionKey, String receiverKey, String username)
	{
		try
		{
//			System.out.println("1) checkNewChats starting .."); 
			return (PollNewChatsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PollNewChatsRequest(sessionKey, receiverKey, username)));
//			ServerMessage msg = RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PollNewChatsRequest(sessionKey, receiverKey, username));
			
//			System.out.println("1) checkNewChats: msg Received ...");
//			PollNewChatsResponse response = (PollNewChatsResponse)msg;
//			System.out.println("2) checkNewChats: msg Casting DONE ...");
//			System.out.println("2.1) response.getType() = " + response.getType());
			/*
			System.out.println("2.11) response.getMessages().size() = " + response.getMessages().size());
			for (int i = 0; i < response.getMessages().size(); i++)
			{
				System.out.println(response.getMessages().get(i).getSenderName());
				System.out.println(response.getMessages().get(i).getMessage());
				System.out.println(response.getMessages().get(i).getReceiverKey());
			}
			System.out.println("3) checkNewChats: msg Casting DONE ...");
			
			return (PollNewChatsResponse)msg;
			*/
//			return response;
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
//			e.printStackTrace();
//			System.out.println("2) checkNewChats done .."); 
			ServerStatus.FREE().setShutdown();
			ServerStatus.FREE().printException(e);
		}
//		System.out.println("3) checkNewChats done .."); 
		return ChatMessageConfig.NO_POLL_NEW_CHATS_RESPONSE;
	}
}
