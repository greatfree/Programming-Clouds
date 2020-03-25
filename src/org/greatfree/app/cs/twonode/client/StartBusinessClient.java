package org.greatfree.app.cs.twonode.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 07/27/2018, Bing Li
public class StartBusinessClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		Scanner in = new Scanner(System.in);
		
		BusinessReader.RR().init();
		BusinessEventer.NOTIFY().init();
		BusinessReader.RR().query("Do you have Android phones?");
		BusinessEventer.NOTIFY().notifyOrder(true);

		System.out.println("Tell your role: v or c");
		String isVendor = in.nextLine();
		if (isVendor.equals("v"))
		{
			BusinessEventer.NOTIFY().post("amazon", "iphone", 100);
		}
		else
		{
			Scheduler.GREATFREE().init(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);
			Scheduler.GREATFREE().submit(new PollMerchandise(), ChatConfig.CHAT_POLLING_DELAY, ChatConfig.CHAT_POLLING_PERIOD);
		}

		System.out.println("Press any key to shutdown the client and the server ...");
		in.nextLine();
		if (isVendor.equals("v"))
		{
			System.out.println("The vendor is shutting down the client and the server ...");
			BusinessEventer.NOTIFY().shutdown();
		}

		BusinessEventer.NOTIFY().dispose(RegistryConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		BusinessReader.RR().shutdown();

		if (!isVendor.equals("v"))
		{
			Scheduler.GREATFREE().shutdown(RegistryConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
		}

		in.close();
	}

}
