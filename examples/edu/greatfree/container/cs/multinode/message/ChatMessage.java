package edu.greatfree.container.cs.multinode.message;

import java.io.Serializable;
import java.util.Date;

/*
 * The class keeps one piece of message for a chat. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
public class ChatMessage implements Serializable
{
	private static final long serialVersionUID = 865644443413971395L;

	// The key of the message. 04/23/2017, Bing Li
//	private final String key;
//	private String key;
	// The sender of the message. 04/23/2017, Bing Li
//	private final String senderKey;
	private String senderKey;
	// The sender's name. 04/24/2017, Bing Li
//	private final String senderName;
	private String senderName;
	// The receiver of the message. 04/23/2017, Bing Li
//	private final String receiverKey;
	private String receiverKey;
	// The message itself. 04/23/2017, Bing Li
//	private final String message;
	private String message;
	// The flag indicates whether the message is checked by partners or not. 04/23/2017, Bing Li
//	private AtomicBoolean isNew;
	// The time of the message being created. 04/23/2017, Bing Li
	private Date time;
	
	/*
	 * The constructor. 04/23/2017, Bing Li
	 */
//	public ChatMessage(String key, String senderKey, String senderName, String receiverKey, String message, Date time)
	public ChatMessage(String senderKey, String senderName, String receiverKey, String message, Date time)
	{
		// The key is unique key. 04/23/2017, Bing Li
//		this.key = Tools.generateUniqueKey();
//		this.key = key;
		this.senderKey = senderKey;
		this.senderName = senderName;
		this.receiverKey = receiverKey;
		this.message = message;
//		this.isNew = new AtomicBoolean(true);
//		this.time = Calendar.getInstance().getTime();
		this.time = time;
	}

	/*
	public String getKey()
	{
		return this.key;
	}
	*/
	
	public String getSenderKey()
	{
		return this.senderKey;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
	
	public String getReceiverKey()
	{
		return this.receiverKey;
	}
	
	public String getMessage()
	{
		return this.message;
	}

	/*
	public boolean isNew()
	{
		return this.isNew.get();
	}
	
	public void setOld()
	{
		this.isNew.set(false);
	}
	*/
	
	public Date getTime()
	{
		return this.time;
	}
}
