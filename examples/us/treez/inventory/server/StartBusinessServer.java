package us.treez.inventory.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import us.treez.inventory.TreezConfig;

// Created: 02/05/2020, Bing Li
class StartBusinessServer
{

	public static void main(String[] args)
	{
		System.out.println("Treez server starting up ...");
		
		try
		{
			BusinessServer.CS().start(TreezConfig.SERVER_PORT, new BusinessTask(), TreezConfig.INVENTORY_DB_PATH, TreezConfig.ORDER_DB_PATH);
		}
		catch (IOException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("Treez server started ...");

		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				Thread.sleep(TreezConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
