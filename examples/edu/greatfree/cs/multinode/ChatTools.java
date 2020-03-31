package edu.greatfree.cs.multinode;

import org.greatfree.util.Tools;

/*
 * The tool is shared by the client, the server and even the administrator for chatting. 04/27/2017, Bing Li
 */

// Created: 04/27/2017, Bing Li
public class ChatTools
{
	public static String getChatSessionKey(String senderKey, String receiverKey)
	{
		if (senderKey.compareTo(receiverKey) > 0)
		{
			return Tools.getHash(senderKey + receiverKey);
		}
		else
		{
			return Tools.getHash(receiverKey + senderKey);
		}
	}
	
	public static String getUserKey(String username)
	{
		return Tools.getHash(username);
	}
}
