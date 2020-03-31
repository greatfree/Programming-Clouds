package edu.greatfree.tncs.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.tncs.message.MerchandiseResponse;

// Created: 05/01/2019, Bing Li
class StartReader
{
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Press Enter to start ...");
		in.nextLine();
		ECommerceReader.CS().init();
		try
		{
			MerchandiseResponse response = ECommerceReader.CS().read("GF-01-12345");
			System.out.println("ID = " + response.getMerchandise().getMerchandiseID());
			System.out.println("Name = " + response.getMerchandise().getMerchandiseName());
			System.out.println("Description = " + response.getMerchandise().getDescription());
			System.out.println("Price = " + response.getMerchandise().getPrice());
			System.out.println("In Stock = " + response.getMerchandise().getInStock());
			System.out.println("Manufacturer = " + response.getMerchandise().getManufacturer());
			System.out.println("Shipping Manner = " + response.getMerchandise().getShippingManner());
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Press Enter to exit ...");
		in.nextLine();
		try
		{
			ECommerceReader.CS().shutdown();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		in.close();
	}
}
