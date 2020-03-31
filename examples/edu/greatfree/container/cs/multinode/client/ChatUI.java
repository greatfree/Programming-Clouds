package edu.greatfree.container.cs.multinode.client;

import java.util.Scanner;

import org.greatfree.client.StandaloneClient;

import edu.greatfree.container.cs.multinode.message.ChatNotification;
import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.cs.multinode.ChatMenu;
import edu.greatfree.cs.multinode.ChatOptions;
import edu.greatfree.cs.multinode.ChatTools;

//Created: 01/07/2019, Bing Li, in the airplane from Zhuhai to Xi'An
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
	
	public static ChatUI CS_CONTAINER()
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
		System.out.println(ChatMenu.TYPE_MESSAGE + ChatMaintainer.CS_CONTAINER().getPartner());
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
				StandaloneClient.CS().asyncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatNotification(ChatTools.getChatSessionKey(ChatMaintainer.CS_CONTAINER().getLocalUserKey(), ChatMaintainer.CS_CONTAINER().getPartnerKey()), message, ChatMaintainer.CS_CONTAINER().getLocalUserKey(), ChatMaintainer.CS_CONTAINER().getLocalUsername(), ChatMaintainer.CS_CONTAINER().getPartnerKey(), ChatMaintainer.CS_CONTAINER().getPartner()));
				break;
				
			case ChatOptions.QUIT_CHAT:
				break;
		}
	}
}
