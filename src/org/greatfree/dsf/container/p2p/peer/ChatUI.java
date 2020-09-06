package org.greatfree.dsf.container.p2p.peer;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.dsf.container.p2p.message.ChatNotification;
import org.greatfree.dsf.p2p.peer.ChatMaintainer;

// Created: 01/12/2019, Bing Li
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
	
	public static ChatUI CONTAINER()
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
		System.out.println(ChatMenu.TYPE_MESSAGE + ChatMaintainer.PEER().getPartner());
		System.out.println(ChatMenu.QUIT);
		System.out.println(ChatMenu.MENU_TAIL);
		System.out.println(ChatMenu.INPUT_PROMPT);
	}

	/*
	 * Send messages to the chatting peer. 04/23/2017, Bing Li
	 */
	public void sent(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			// Send the chatting message to the chatting partner. 06/11/2017, Bing Li
			case ChatOptions.TYPE_CHAT:
				System.out.println("Please type your message: ");
				String message = in.nextLine();
				ChatPeer.CONTAINER().notify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ChatNotification(message, ChatMaintainer.PEER().getLocalUsername()));
				break;

			case ChatOptions.QUIT_CHAT:
				break;
		}
	}
}
