package us.treez.inventory.client;

import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

import us.treez.inventory.Inventory;
import us.treez.inventory.Order;
import us.treez.inventory.TreezConfig;
import us.treez.inventory.message.AddInventoryNotification;
import us.treez.inventory.message.MerchandiseRequest;
import us.treez.inventory.message.MerchandiseResponse;
import us.treez.inventory.message.OrderRequest;
import us.treez.inventory.message.OrderResponse;
import us.treez.inventory.message.UpdateInventoryNotification;

// Created: 02/05/2020, Bing Li
class StartBusinessClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();
		Scanner in = new Scanner(System.in);
		System.out.println("Are you vendor? (y/n)");
		String option = in.nextLine();
		if (option.equals("y"))
		{
			StandaloneClient.CS().syncNotify(TreezConfig.SERVER_IP, TreezConfig.SERVER_PORT, new AddInventoryNotification(new Inventory("001", "PC", "Dell", 239.99f, 100)));
			System.out.println("Inventory is added");
			StandaloneClient.CS().syncNotify(TreezConfig.SERVER_IP, TreezConfig.SERVER_PORT, new UpdateInventoryNotification(new Inventory("001", "PC", "Dell", 239.99f, 150)));
			System.out.println("Inventory is updated");
			MerchandiseResponse mr = (MerchandiseResponse)StandaloneClient.CS().read(TreezConfig.SERVER_IP, TreezConfig.SERVER_PORT, new MerchandiseRequest("001"));
			System.out.println(mr.getInventory());
		}
		else
		{
			MerchandiseResponse mr = (MerchandiseResponse)StandaloneClient.CS().read(TreezConfig.SERVER_IP, TreezConfig.SERVER_PORT, new MerchandiseRequest("001"));
			System.out.println(mr.getInventory());
			OrderResponse or = (OrderResponse)StandaloneClient.CS().read(TreezConfig.SERVER_IP, TreezConfig.SERVER_PORT, new OrderRequest(new Order("001", "bing.li@asu.edu", Calendar.getInstance().getTime(), "Normal", 2, 2 * 239.99f)));
			if (or.isSufficient())
			{
				System.out.println("2 PCs are ordered successfully");
			}
			else
			{
				System.out.println("2 PCs are falied to be ordered");
			}
			mr = (MerchandiseResponse)StandaloneClient.CS().read(TreezConfig.SERVER_IP, TreezConfig.SERVER_PORT, new MerchandiseRequest("001"));
			System.out.println(mr.getInventory());
		}

		StandaloneClient.CS().dispose();
		in.close();
	}

}
