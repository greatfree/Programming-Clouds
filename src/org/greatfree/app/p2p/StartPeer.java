package org.greatfree.app.p2p;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.app.p2p.message.GreetingResponse;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 08/19/2018, Bing Li
class StartPeer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		Scanner in = new Scanner(System.in);

		System.out.println("Tell me your user name: ");

		String username = in.nextLine();
		
		System.out.println("Tell me your partner: ");
		
		String partner = in.nextLine();
		
		PeerSingleton.PEER().start(username);

		PeerSingleton.PEER().registerChat(Tools.getHash(username), username, username + " is a football fan!", "Programming");
		
		System.out.println("Press enter to search user ...");
		
		in.nextLine();

		ChatPartnerResponse response = PeerSingleton.PEER().searchUser(Tools.getHash(partner));
		
		String ip = response.getIP();
		int port = response.getPort();
		
		System.out.println(partner + ": IP = " + ip + ", port = " + port);
		
		System.out.println("Press enter to greet ...");
		
		in.nextLine();
		
		GreetingResponse g;
		for (int i = 0; i < 5; i++)
		{
			g = PeerSingleton.PEER().greet(ip, port, "What's up?");
			System.out.println(partner + ": " + g.getAppreciation());
		}
		
		System.out.println("Press enter to hello ...");
		
		in.nextLine();
		
		for (int i = 0; i < 5; i++)
		{
			PeerSingleton.PEER().hello(ip, port, "How you doing?");
		}
		
		System.out.println("Press enter to stop ...");
		
		in.nextLine();
	
		PeerSingleton.PEER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		in.close();
	}

}
