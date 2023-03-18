package org.greatfree.framework.cluster.multicast.client;

import java.io.IOException;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Tools;

/**
 * 
 * Revised for getting involved with the container-registry on 10/08/2022, Bing Li
 * 
 * @author libing
 * 
 * 10/01/2022
 *
 */
final class StartClient
{
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, RemoteIPNotExistedException
	{
		int option = MenuOptions.NO_OPTION;
		String optionStr;
		
		ClusterClient.MULTI().init();
		ClusterUI.MULTI().init();
		
		while (option != MenuOptions.QUIT)
		{
			ClusterUI.MULTI().printMenu();
			optionStr = Tools.INPUT.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				ClusterUI.MULTI().execute(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClusterMenu.WRONG_OPTION);
			}
		}
		ClusterClient.MULTI().dispose();
	}
}

