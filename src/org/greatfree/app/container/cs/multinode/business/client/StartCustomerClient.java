package org.greatfree.app.container.cs.multinode.business.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.app.container.cs.multinode.business.message.MerchandiseRequest;
import org.greatfree.app.container.cs.multinode.business.message.MerchandiseResponse;
import org.greatfree.app.container.cs.multinode.business.message.ShutdownBusinessServerNotification;
import org.greatfree.chat.ChatConfig;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/24/2019, Bing Li
class StartCustomerClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();

		Scanner in = new Scanner(System.in);

		System.out.println("Press any key to query merchandises ...");

		in.nextLine();

		MerchandiseResponse res = (MerchandiseResponse)StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new MerchandiseRequest("Java"));
		
		System.out.println(res.getMerchandise().getName());
		System.out.println(res.getMerchandise().getInStock());
		System.out.println(res.getMerchandise().getPrice());
		
		
		System.out.println("Press any key to shutdown the business server ...");

		in.nextLine();
		
		StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ShutdownBusinessServerNotification());
		
		System.out.println("Press any key to exit ...");

		StandaloneClient.CS().dispose();

		in.close();
	}

}
