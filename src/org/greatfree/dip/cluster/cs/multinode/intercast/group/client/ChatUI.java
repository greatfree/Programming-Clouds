package org.greatfree.dip.cluster.cs.multinode.intercast.group.client;

import java.util.Scanner;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.dip.cluster.cs.multinode.intercast.group.message.GroupChatNotification;
import org.greatfree.dip.cluster.cs.twonode.client.ChatClient;

// Created: 04/07/2019, Bing Li
class ChatUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/27/2017, Bing Li
	 */
	private ChatUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ChatUI instance = new ChatUI();
	
	public static ChatUI GROUP()
	{
		if (instance == null)
		{
			instance = new ChatUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	public void printMenu()
	{
		System.out.println(ChatMenu.MENU_HEAD);
		System.out.println(ChatMenu.TYPE_MESSAGE + ChatMaintainer.GROUP().getGroupName());
		System.out.println(ChatMenu.QUIT);
		System.out.println(ChatMenu.MENU_TAIL);
		System.out.println(ChatMenu.INPUT_PROMPT);
	}
	
	public void sent(int option)
	{
		switch (option)
		{
			case ChatOptions.TYPE_CHAT:
				System.out.println("Please type your message: ");
				String message = in.nextLine();
				System.out.println("ChatUI members' count = " + ChatMaintainer.GROUP().getMembers().size());
				ChatClient.CONTAINER().asyncNotify(new GroupChatNotification(ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getLocalUserKey(), ChatMaintainer.GROUP().getMembers(), message, ChatMaintainer.GROUP().getLocalUsername()));
				break;
				
			case ChatOptions.QUIT_CHAT:
				break;
		}
	}
	

}
