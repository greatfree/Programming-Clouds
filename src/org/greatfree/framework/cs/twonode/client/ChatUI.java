package org.greatfree.framework.cs.twonode.client;

import java.util.Scanner;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;

// Created: 05/13/2018, Bing Li
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
	
	public static ChatUI CS()
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
				ChatEventer.RE().notifyChat(message);
				break;
				
			case ChatOptions.QUIT_CHAT:
				break;
		}
	}
}
