package org.greatfree.app.cs.twonode.client;

import java.io.IOException;

import org.greatfree.app.cs.twonode.message.MerchandisePollResponse;
import org.greatfree.exceptions.RemoteReadException;

// Created: 07/31/2018, Bing Li
public class PollMerchandise implements Runnable
{

	@Override
	public void run()
	{
		try
		{
			MerchandisePollResponse response = BusinessReader.RR().poll("iphone", 2);
			if (response.isAvailable())
			{
				System.out.println("iphone/2 is available");
				return;
			}
			else
			{
				System.out.println("iphone/2 is NOT available");
			}
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		
	}

}
