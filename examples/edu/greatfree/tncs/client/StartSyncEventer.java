package edu.greatfree.tncs.client;

import java.io.IOException;
import java.util.Scanner;

import edu.greatfree.tncs.message.Merchandise;

// Created: 05/18/2019, Bing Li
class StartSyncEventer
{
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Press Enter to start ...");
		in.nextLine();

		SyncECommerceEventer.CS().init();
		try
		{
			SyncECommerceEventer.CS().postMerchandise(new Merchandise("#001", "GreatFree Smartphone", "GF-01-12345", "This is a phone powered by GreatFree", 899.0f, 100, "GreatFree Co.", "Ground Shipping"));
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("Press Enter to exit ...");
		in.nextLine();
		try
		{
			SyncECommerceEventer.CS().dispose();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		in.close();
	}
}
