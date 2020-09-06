package org.greatfree.dsf.cluster.cs.twonode.clusterclient;

import java.util.Scanner;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.chat.ChatTools;
import org.greatfree.dsf.cluster.original.cs.twonode.message.ChatNotification;
import org.greatfree.dsf.cs.twonode.client.ChatMaintainer;

// Created: 01/15/2019, Bing Li
public class ChatUI
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
	
	public static ChatUI CCC()
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

	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ChatMenu.MENU_HEAD);
		System.out.println(ChatMenu.TYPE_MESSAGE + ChatMaintainer.CS().getPartner());
		System.out.println(ChatMenu.QUIT);
		System.out.println(ChatMenu.MENU_TAIL);
		System.out.println(ChatMenu.INPUT_PROMPT);
	}

	/*
	 * Send messages to the chatting server. 04/23/2017, Bing Li
	 */
	public void sent(int option)
	{
		switch (option)
		{
			case ChatOptions.TYPE_CHAT:
				System.out.println("Please type your message: ");
				String message = in.nextLine();
				ChatClient.CCC().asyncNotify(new ChatNotification(ChatTools.getChatSessionKey(ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getPartnerKey()), message, ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getLocalUsername(), ChatMaintainer.CS().getPartnerKey(), ChatMaintainer.CS().getPartner()));
				break;
				
			case ChatOptions.QUIT_CHAT:
				break;
		}
	}
}
