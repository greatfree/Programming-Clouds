package org.greatfree.app.container.cs.multinode.library.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.app.container.cs.multinode.library.message.ReturnBookNotification;
import org.greatfree.chat.ChatConfig;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

//Created: 12/19/2018, Bing Li
class ReturnBook
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();
		
		try
		{
			StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ReturnBookNotification("Java", "Jobs"));
		}
		catch (IOException | InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scanner in = new Scanner(System.in);

		System.out.println("Press any key to exit ...");
		
		in.nextLine();
		
		StandaloneClient.CS().dispose();

		in.close();
	}

}
