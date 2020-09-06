package org.greatfree.dsf.cluster.cs.multinode.intercast.group.clusterserver.child;

import java.io.Serializable;
import java.util.Date;

import org.greatfree.util.Tools;

// Created: 04/05/2019, Bing Li
public class GroupChatMessage implements Serializable
{
	private static final long serialVersionUID = 243129280918112632L;
	
	private String key;
	
	private String senderKey;
	private String senderName;
	private String message;
	private Date time;
	
	public GroupChatMessage(String senderKey, String senderName, String message, Date time)
	{
//		this.key = Tools.getHash(message);
		this.key = Tools.generateUniqueKey();
		this.senderKey = senderKey;
		this.senderName = senderName;
		this.message = message;
		this.time = time;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getSenderKey()
	{
		return this.senderKey;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public Date getTime()
	{
		return this.time;
	}

}
