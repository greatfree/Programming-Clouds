package org.greatfree.framework.admin;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.ChatMessageConfig;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.message.PeerAddressRequest;
import org.greatfree.framework.multicast.message.PeerAddressResponse;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.streaming.StreamConfig;
import org.greatfree.util.IPAddress;
import org.greatfree.util.NodeID;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The class wraps the class, RemoteReader, to send IP requests to the register server to get IPs to the servers or peers to be managed. 04/23/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
class ChatAdminReader
{
	private ChatAdminReader()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static ChatAdminReader instance = new ChatAdminReader();
	
	public static ChatAdminReader RR()
	{
		if (instance == null)
		{
			instance = new ChatAdminReader();
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
	 * Send the request the chatting registry server to retrieve the specified registered chatting partner. 05/28/2017, Bing Li
	 */
	public ChatPartnerResponse searchUser(String userKey)
	{
		try
		{
			return (ChatPartnerResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatPartnerRequest(userKey)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return ChatMessageConfig.NO_PEER_CHAT_PARTNER_RESPONSE;
	}
	
	public IPAddress getPubSubServerIP()
	{
		try
		{
			return ((PeerAddressResponse)RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(Tools.getHash(StreamConfig.PUBSUB_SERVER_NAME)))).getPeerAddress();
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return UtilConfig.NO_IP_ADDRESS;
	}
}
