package org.greatfree.framework.cs.nio.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.nio.CSClient;
import org.greatfree.framework.cs.nio.Config;
import org.greatfree.framework.cs.nio.message.MyNotification;
import org.greatfree.framework.cs.nio.message.StopServerNotification;

/**
 * 
 * @author Bing Li
 * 
 *         02/05/2022
 *
 */
class StartClient
{

	public static void main(String[] args) throws IOException, InterruptedException
	{
//		String serverIP = "192.168.3.8";
		String serverIP = "192.168.1.18";
		int serverPort = 8964;
		Scanner in = new Scanner(System.in);
		CSClient client = new CSClient(Config.BUFFER_SIZE);
		System.out.println("Press enter to send one notification ...");
		in.nextLine();
		client.notify(serverIP, serverPort, new MyNotification("Hello!"));

		System.out.println("Press enter to stop the server ...");
		in.nextLine();
		client.notify(serverIP, serverPort, new StopServerNotification());
		
		System.out.println("Press enter to stop the client ...");
		in.nextLine();
		
		client.close(1000);
		in.close();
	}

}
