package org.greatfree.framework.container.cs.multinode.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.greatfree.chat.ChatMessage;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.multinode.message.AddPartnerNotification;
import org.greatfree.framework.container.cs.multinode.message.ChatNotification;
import org.greatfree.framework.container.cs.multinode.message.ChatPartnerRequest;
import org.greatfree.framework.container.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.framework.container.cs.multinode.message.PollNewChatsRequest;
import org.greatfree.framework.container.cs.multinode.message.PollNewSessionsRequest;
import org.greatfree.framework.cs.multinode.message.ChatPartnerResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.framework.cs.multinode.message.PollNewChatsResponse;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsResponse;
import org.greatfree.framework.cs.multinode.server.CSAccount;
import org.greatfree.framework.cs.multinode.server.PrivateChatSessions;
import org.greatfree.framework.cs.twonode.server.AccountRegistry;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.UtilConfig;

// Created: 01/06/2019, Bing Li
public class ChatServerTask implements ServerTask
{
	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case SystemMessageType.ADD_PARTNER_NOTIFICATION:
				System.out.println("ADD_PARTNER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AddPartnerNotification apn = (AddPartnerNotification)notification;
				PrivateChatSessions.HUNGARY().addSession(apn.getPartnerKey(), apn.getLocalUserKey());
				break;
				
			case SystemMessageType.CHAT_NOTIFICATION:
				System.out.println("CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ChatNotification cn = (ChatNotification)notification;
				// When new messages are available, they are retained in the server for polling. 05/25/2017, Bing Li
				PrivateChatSessions.HUNGARY().addMessage(cn.getSessionKey(), cn.getSenderKey(), cn.getReceiverKey(), cn.getMessage());
				break;
				
			case SystemMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ServerStatus.FREE().setShutdown();
				try
				{
					ChatServer.CS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case SystemMessageType.CHAT_REGISTRY_REQUEST:
					System.out.println("CHAT_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
					ChatRegistryRequest crr = (ChatRegistryRequest)request;
					AccountRegistry.CS().add(new CSAccount(crr.getUserKey(), crr.getUserName(), crr.getDescription()));
				return new ChatRegistryResponse(true);
				
			case SystemMessageType.CHAT_PARTNER_REQUEST:
					System.out.println("CHAT_PARTNER_REQUEST received @" + Calendar.getInstance().getTime());
					ChatPartnerRequest cpr = (ChatPartnerRequest)request;
					// Check whether the account is existed or not? Bing Li
					if (AccountRegistry.CS().isAccountExisted(cpr.getUserKey()))
					{
						// Get the account. 04/16/2017, Bing Li
						CSAccount account = AccountRegistry.CS().getAccount(cpr.getUserKey());
						// Generate the response. 04/16/2017, Bing Li
						return new ChatPartnerResponse(account.getUserKey(), account.getUserName(), account.getDescription());
					}
					else
					{
						// Generate the response. 04/16/2017, Bing Li
						return new ChatPartnerResponse(UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING);
					}
					
			case SystemMessageType.POLL_NEW_SESSIONS_REQUEST:
					System.out.println("POLL_NEW_SESSIONS_REQUEST received @" + Calendar.getInstance().getTime());
					PollNewSessionsRequest pnsr = (PollNewSessionsRequest)request;
					if (PrivateChatSessions.HUNGARY().isSessionExisted(pnsr.getReceiverKey()))
					{
						// Check whether a new exists. 05/24/2017, Bing Li
						PollNewSessionsResponse response = new PollNewSessionsResponse(PrivateChatSessions.HUNGARY().getSessionKeys(pnsr.getReceiverKey()));
						// The polled session keys should be removed. It is not necessary. It depends on how to design your application. 05/24/2017, Bing Li
						PrivateChatSessions.HUNGARY().removeSession(pnsr.getReceiverKey());
						return response;
					}
					else
					{
						return new PollNewSessionsResponse(null);
					}
					
			case SystemMessageType.POLL_NEW_CHATS_REQUEST:
					System.out.println("POLL_NEW_CHATS_REQUEST received @" + Calendar.getInstance().getTime());
					PollNewChatsRequest pncr = (PollNewChatsRequest)request;
					List<ChatMessage> chatMessages = PrivateChatSessions.HUNGARY().getNewMessages(pncr.getChatSessionKey(), pncr.getReceiverKey());
					if (chatMessages != null)
					{
						System.out.println("PollNewChatsThread: " + pncr.getUsername() + " chatting messages are detected ...");
						return new PollNewChatsResponse(chatMessages);
					}
					else
					{
						System.out.println("PollNewChatsThread: " + pncr.getUsername() + " NO chatting messages are existed ...");
						return new PollNewChatsResponse(new ArrayList<ChatMessage>());
					}
		}
		return null;
	}

}
