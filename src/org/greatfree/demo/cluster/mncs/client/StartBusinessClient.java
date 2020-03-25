package org.greatfree.demo.cluster.mncs.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.app.container.cs.multinode.business.message.Merchandise;
import org.greatfree.demo.cluster.mncs.message.MerchandiseRequest;
import org.greatfree.demo.cluster.mncs.message.MerchandiseResponse;
import org.greatfree.demo.cluster.mncs.message.PostMerchandiseNotification;
import org.greatfree.dip.cluster.cs.twonode.client.ChatClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.Tools;

// Created: 02/17/2019, Bing Li
public class StartBusinessClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		ChatClient.CONTAINER().initWithout();

		Scanner in = new Scanner(System.in);
		
		System.out.println("Are you a vendor?");
		
		String isVendor = in.nextLine();
		
		String vn = "greatfree";
//		String cn = "lblabs";
		if (isVendor.equals("y"))
		{
			System.out.println("Post merchandise ...");
			ChatClient.CONTAINER().syncNotify(new PostMerchandiseNotification(vn, new Merchandise("Phone", 100, 15.0f)));
			System.out.println("Merchandise posted ...");
		}
		else
		{
			Response r = (Response)ChatClient.CONTAINER().read(new MerchandiseRequest(vn, "Phone"));
			List<MerchandiseResponse> rs = Tools.filter(r.getResponses(), MerchandiseResponse.class);
			for (MerchandiseResponse entry : rs)
			{
				if (entry.getMerchandise() != null)
				{
					System.out.println(entry.getMerchandise().getName() + ", " + entry.getMerchandise().getInStock() + ", " + entry.getMerchandise().getPrice());
				}
			}
		}
		
		System.out.println("Press any key to exit ...");
		
		in.nextLine();

		ChatClient.CONTAINER().dispose();

		in.close();
	}

}
