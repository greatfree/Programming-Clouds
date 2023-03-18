package org.greatfree.demo.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.StandaloneClient;
import org.greatfree.demo.message.MerchandiseRequest;
import org.greatfree.demo.message.MerchandiseResponse;
import org.greatfree.demo.message.ShutdownBusinessServerNotification;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/24/2019, Bing Li
class StartCustomerClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, InterruptedException, RemoteIPNotExistedException, IOException
	{
		Scanner in = new Scanner(System.in);

		StandaloneClient.CS().init();

		System.out.println("Press any key to query merchandises ...");
		
		in.nextLine();
		
		MerchandiseResponse res = (MerchandiseResponse)StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new MerchandiseRequest("Java"));
		
		System.out.println(res.getMerchandise().getName());
		System.out.println(res.getMerchandise().getInStock());
		System.out.println(res.getMerchandise().getPrice());
		
		System.out.println("Press any key to shutdown the server ...");

		in.nextLine();
		
		StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ShutdownBusinessServerNotification());
		
		System.out.println("Press any key to exit ...");
		
		in.nextLine();

		StandaloneClient.CS().dispose();
		
		in.close();

	}

}
