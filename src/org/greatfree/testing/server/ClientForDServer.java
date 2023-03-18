package org.greatfree.testing.server;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.message.DRequest;
import org.greatfree.testing.message.DResponse;
import org.greatfree.testing.message.ShutdownDServerNotification;

// Created: 03/30/2020, Bing Li
class ClientForDServer
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, RemoteIPNotExistedException
	{
		StandaloneClient.CS().init();
		Scanner in = new Scanner(System.in);
		
		String option = "";
		do
		{
			System.out.println("Press Enter to send a request ...");
			option = in.nextLine();

			DResponse res = (DResponse)StandaloneClient.CS().read(ServerConfig.SERVER_IP, ServerConfig.PORT_1, new DRequest("Hello"));
			
			if (res != null)
			{
				System.out.println("res = " + res.isSucceeded());
			}
			else
			{
				System.out.println("res is NULL");
			}
			
		}
		while (!option.equals("q"));

		System.out.println("Press Enter to shutdown the sever ...");
		in.nextLine();
		StandaloneClient.CS().syncNotify(ServerConfig.SERVER_IP, ServerConfig.PORT_1, new ShutdownDServerNotification());

		System.out.println("Press Enter to exit ...");
		in.nextLine();
		StandaloneClient.CS().dispose();
		in.close();
	}

}
