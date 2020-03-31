package edu.greatfree.cs.multinode.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Sets;

import edu.greatfree.container.cs.multinode.message.ChatMessage;

/*
 * It keeps all of the messages in a session. As a simulation system, it is too simple. Actually, the ranking by time and a persistent mechanism are required for a practical system. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
class ChatSession
{
	// The unique key of the session. 04/23/2017, Bing Li
	private final String sessionKey;
	
	// The chatting partners of the session. 04/23/2017, Bing Li
	private Set<String> partners;

	// The messages in the session. 04/23/2017, Bing Li
//	private Map<String, ChatMessage> messages;
	private List<ChatMessage> messages;
	private ReentrantLock lock;

	/*
	 * The constructor. 04/23/2017, Bing Li
	 */
	public ChatSession(String sessionKey)
	{
		// Generate the unique key. 04/23/2017, Bing Li
//		this.key = Tools.generateUniqueKey();
		this.sessionKey = sessionKey;
		this.partners = Sets.newHashSet();
		
		this.messages = new ArrayList<ChatMessage>();
		this.lock = new ReentrantLock();
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public Set<String> getPartners()
	{
		this.lock.lock();
		try
		{
			return this.partners;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	/*
	 * Add a new partner to the chatting session. 04/23/2017, Bing Li
	 */
	public void addPartner(String userKey)
	{
		this.lock.lock();
		this.partners.add(userKey);
		this.lock.unlock();
	}

	/*
	 * Check whether one user participates the chatting. 04/23/2017, Bing Li
	 */
	public boolean isPartnerExisted(String userKey)
	{
		this.lock.lock();
		try
		{
			return this.partners.contains(userKey);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/*
	 * Add a new message. 04/23/2017, Bing Li
	 */
	public void addMessage(String senderKey, String receiverKey, String message)
	{
		/*
		ChatMessage chatMsg = new ChatMessage(Tools.generateUniqueKey(), senderKey, AccountRegistry.MANAGEMENT().getAccount(senderKey).getUserName(), receiverKey, message, Calendar.getInstance().getTime());
		this.messages.put(chatMsg.getKey(), chatMsg);
		*/
		
//		System.out.println("ChatSession-addMessage(): senderKey = " + senderKey);
//		System.out.println("ChatSession-addMessage(): userName = " + AccountRegistry.CS().getAccount(senderKey).getUserName());
		
		this.lock.lock();
//		this.messages.add(new ChatMessage(Tools.generateUniqueKey(), senderKey, AccountRegistry.MANAGEMENT().getAccount(senderKey).getUserName(), receiverKey, message, Calendar.getInstance().getTime()));
		this.messages.add(new ChatMessage(senderKey, AccountRegistry.CS().getAccount(senderKey).getUserName(), receiverKey, message, Calendar.getInstance().getTime()));
		this.lock.unlock();
	}

	/*
	 * Expose all messages. 04/23/2017, Bing Li
	 */
//	public Collection<ChatMessage> getMessages()
//	public Map<String, ChatMessage> getMessages()
//	public List<String> getMessages()
	public List<ChatMessage> getMessages()
	{
//		List<ChatMessage> messages = new ArrayList<ChatMessage>();
//		Map<String, ChatMessage> messages = new HashMap<String, ChatMessage>(this.messages);
		/*
		List<String> messages = new ArrayList<String>();
		for (ChatMessage entry : this.messages.values())
		{
			messages.add(entry.getMessage());
		}
		return messages;
		*/
		this.lock.lock();
		try
		{
			return this.messages;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	public void removeMessage(int index)
	{
		this.lock.lock();
		try
		{
			this.messages.remove(index);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/*
	 * Clear old messages. 04/24/2017, Bing Li
	 */
	public void clearMessages()
	{
		this.lock.lock();
		this.messages.clear();
		this.lock.unlock();
	}

	/*
	 * The accessed messages should be set as old. 04/23/2017, Bing Li
	 */
	/*
	public void setMessageOld(String key)
	{
		this.messages.get(key).setOld();
	}
	*/
}
