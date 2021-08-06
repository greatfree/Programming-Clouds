package org.greatfree.app.search.container.cs.multinode.client.user;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.app.search.container.cluster.message.SearchMultiRequest;
import org.greatfree.app.search.cs.multinode.client.user.SearchMenu;
import org.greatfree.app.search.cs.multinode.client.user.SearchOptions;
import org.greatfree.app.search.multicast.message.Page;
import org.greatfree.app.search.multicast.message.SearchMultiResponse;
import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Tools;

// Created: 01/14/2019, Bing Li
class SearchUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/27/2017, Bing Li
	 */
	private SearchUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static SearchUI instance = new SearchUI();
	
	public static SearchUI CLUSTER()
	{
		if (instance == null)
		{
			instance = new SearchUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(SearchMenu.MENU_HEAD);
		System.out.println(SearchMenu.TYPE_MESSAGE );
		System.out.println(SearchMenu.QUIT);
		System.out.println(SearchMenu.MENU_TAIL);
		System.out.println(SearchMenu.INPUT_PROMPT);
	}

	/*
	 * Send messages to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException
	{
		CollectedClusterResponse response;
		List<SearchMultiResponse> searchResponses;
		switch (option)
		{
			case SearchOptions.TYPE_QUERY:
				System.out.println("Please input your search query: ");
				String keyword = in.nextLine();
				response = (CollectedClusterResponse)StandaloneClusterClient.CONTAINER().readRoot(new SearchMultiRequest("greatfree", keyword));
				searchResponses = Tools.filter(response.getResponses(), SearchMultiResponse.class);
				for (SearchMultiResponse entry : searchResponses)
				{
					for (Page page : entry.getPages())
					{
						System.out.println(page.getTitle() + ", " + page.getText() + ", " + page.getURL());
					}
				}
				break;
				
			case SearchOptions.QUIT_SEARCH:
				break;
		}
	}

}
