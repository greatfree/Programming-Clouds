package org.greatfree.demo.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.StandaloneClient;
import org.greatfree.demo.message.Merchandise;
import org.greatfree.demo.message.PostMerchandiseNotification;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/24/2019, Bing Li
class StartVendorClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		Scanner in = new Scanner(System.in);

		StandaloneClient.CS().init();

		System.out.println("Press any key to post merchandises ...");
		
		in.nextLine();
		
		StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PostMerchandiseNotification(new Merchandise("Java", 10, 20.3f)));

		System.out.println("Press any key to exit ...");
		
		in.nextLine();
		
		StandaloneClient.CS().dispose();
		
		in.close();
		
	}

}
