package us.treez.inventory.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

import us.treez.inventory.TreezConfig;
import us.treez.inventory.message.StopServerNotification;

// Created: 02/06/2020, Bing Li
class StartAdmin
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();
		Scanner in = new Scanner(System.in);
		System.out.println("Press Enter to stop the server ...");
		in.nextLine();
		StandaloneClient.CS().syncNotify(TreezConfig.SERVER_IP	, TreezConfig.SERVER_PORT, new StopServerNotification());
		System.out.println("Press Enter to exit ...");
		in.nextLine();
		StandaloneClient.CS().dispose();
		in.close();
	}

}
