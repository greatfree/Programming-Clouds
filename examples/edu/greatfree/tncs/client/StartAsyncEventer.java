package edu.greatfree.tncs.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.data.ClientConfig;

import edu.greatfree.tncs.message.Merchandise;

// Created: 05/18/2019, Bing Li
class StartAsyncEventer
{
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Press Enter to start ...");
		in.nextLine();

		AsyncECommerceEventer.CS().init();
		AsyncECommerceEventer.CS().postMerchandise(new Merchandise("#001", "GreatFree Smartphone", "GF-01-12345", "This is a phone powered by GreatFree", 899.0f, 100, "GreatFree Co.", "Ground Shipping"));
		System.out.println("Press Enter to exit ...");
		in.nextLine();
		try
		{
			AsyncECommerceEventer.CS().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		}
		catch (InterruptedException | IOException e)
		{
			e.printStackTrace();
		}
		in.close();
	}
}
