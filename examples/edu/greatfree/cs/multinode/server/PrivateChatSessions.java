package edu.greatfree.cs.multinode.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import edu.greatfree.container.cs.multinode.message.ChatMessage;
import edu.greatfree.cs.multinode.ChatTools;

/*
 * The class keeps the chatting sessions for private users. In the case, the private chatting is defined as the one that happens between two users only. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
public class PrivateChatSessions
{
	// The map contains the new chatting messages. The key is the chatting session key and the value of the session that contains the new chatting messages. 04/24/2017, Bing Li
	private Map<String, ChatSession> chats;
	// The map contains the new sessions. The key is the receiver key and the value is the new session key just created. 04/24/2017, Bing Li
	private Map<String, Set<String>> newSessions;
	
	private PrivateChatSessions()
	{
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static PrivateChatSessions instance = new PrivateChatSessions();
	
	public static PrivateChatSessions HUNGARY()
	{
		if (instance == null)
		{
			instance = new PrivateChatSessions();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the free client pool. 04/17/2017, Bing Li
	 */
	public void dispose()
	{
		this.chats.clear();
		this.chats = null;
	}

	/*
	 * Initialize explicitly. 04/23/2017, Bing Li
	 */
	public void init()
	{
		this.chats = new ConcurrentHashMap<String, ChatSession>();
		this.newSessions = new ConcurrentHashMap<String, Set<String>>();
	}

	/*
	 * Add a new message. 04/23/2017, Bing Li
	 */
	public void addMessage(String sessionKey, String senderKey, String receiverKey, String message)
	{
//		System.out.println("addMessage(): sessionKey = " + sessionKey);
//		System.out.println("addMessage(): message = " + message);
//		System.out.println("addMessage(): senderKey = " + senderKey);
//		System.out.println("addMessage(): receiverKey = " + receiverKey);
//		String key = Tools.generateUniqueKey();
		if (!this.chats.containsKey(sessionKey))
		{
			this.chats.put(sessionKey, new ChatSession(sessionKey));
			this.chats.get(sessionKey).addMessage(senderKey, receiverKey, message);
		}
		else
		{
			this.chats.get(sessionKey).addMessage(senderKey, receiverKey, message);
		}
	}
	
	/*
	 * Get new messages for the receiver. 04/23/2017, Bing Li
	 */
	public List<ChatMessage> getNewMessages(String sessionKey, String receiverKey)
	{
		if (this.chats.containsKey(sessionKey))
		{
			if (this.chats.get(sessionKey).isPartnerExisted(receiverKey))
			{
				List<ChatMessage> newMessages = new ArrayList<ChatMessage>();
				List<Integer> removedIndexes = new ArrayList<Integer>();
				int index = 0;
				for (ChatMessage message : this.chats.get(sessionKey).getMessages())
				{
					if (message.getReceiverKey().equals(receiverKey))
					{
						newMessages.add(message);
						removedIndexes.add(index);
					}
					index++;
				}
				for (Integer entry : removedIndexes)
				{
					this.chats.get(sessionKey).removeMessage(entry);
				}
//				System.out.println("1.8) PrivateChatSessions-getNewMessages(): ...");
				return newMessages;
			}
		}
//		System.out.println("2) PrivateChatSessions-getNewMessages(): ...");
		return null;
	}
	
	/*
	 * Remove one chatting session. 12/09/2017, Bing Li
	 */
	public void removeMessage(String sessionKey)
	{
		this.chats.get(sessionKey).clearMessages();
	}

	/*
	 * Add new sessions. 04/24/2017, Bing Li
	 */
	public void addSession(String receiverKey, String senderKey)
	{
		
		if (!this.newSessions.containsKey(receiverKey))
		{
			Set<String> sessionKeys = Sets.newHashSet();
			this.newSessions.put(receiverKey, sessionKeys);
		}
		String sessionKey = ChatTools.getChatSessionKey(senderKey, receiverKey);
		if (!this.chats.containsKey(sessionKey))
		{
			this.chats.put(sessionKey, new ChatSession(sessionKey));
			this.chats.get(sessionKey).addPartner(senderKey);
			this.chats.get(sessionKey).addPartner(receiverKey);
		}
		this.newSessions.get(receiverKey).add(sessionKey);
	}

	/*
	 * Is one session available? 04/24/2017, Bing Li
	 */
	public boolean isSessionExisted(String receiverKey)
	{
		return this.newSessions.containsKey(receiverKey);
	}

	/*
	 * Get the existing session keys. 04/24/2017, Bing Li
	 */
	public Set<String> getSessionKeys(String receiverKey)
	{
		return this.newSessions.get(receiverKey);
	}

	/*
	 * Remove the session. 04/24/2017, Bing Li
	 */
	public void removeSession(String receiverKey)
	{
		this.newSessions.remove(receiverKey);
	}
}
