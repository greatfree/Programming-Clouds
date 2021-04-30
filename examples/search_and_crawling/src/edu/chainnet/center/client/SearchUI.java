package edu.chainnet.center.client;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.Tools;

import edu.chainnet.center.PageIndex;
import edu.chainnet.center.message.PauseIndexingNotification;
import edu.chainnet.center.message.ResumeIndexingNotification;
import edu.chainnet.center.message.SearchRequest;
import edu.chainnet.center.message.SearchResponse;

// Created: 04/29/2021, Bing Li
class SearchUI
{
	private Scanner in = new Scanner(System.in);

	private SearchUI()
	{
	}

	private static SearchUI instance = new SearchUI();
	
	public static SearchUI CENTER()
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

	public void printMenu()
	{
		System.out.println(SearchMenu.MENU_HEAD);
		System.out.println(SearchMenu.PAUSE_INDEXING );
		System.out.println(SearchMenu.TYPE_QUERY );
		System.out.println(SearchMenu.RESUME_INDEXING);
		System.out.println(SearchMenu.QUIT);
		System.out.println(SearchMenu.MENU_TAIL);
		System.out.println(SearchMenu.INPUT_PROMPT);
	}

	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response response;
		List<SearchResponse> responses;
		switch (option)
		{
			case SearchOptions.PAUSE_INDEXING:
				System.out.println("PAUSE_INDEXING received @" + Calendar.getInstance().getTime());
				try
				{
					StandaloneClusterClient.CONTAINER().syncNotifyRoot(new PauseIndexingNotification());
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;

			case SearchOptions.TYPE_QUERY:
				System.out.println("Please input your search query: ");
				String keyword = in.nextLine();
				response = (Response)StandaloneClusterClient.CONTAINER().readRoot(new SearchRequest(keyword));
				responses = Tools.filter(response.getResponses(), SearchResponse.class);
				for (SearchResponse entry : responses)
				{
					System.out.println("pages size = " + entry.getPages().size());
					for (PageIndex page : entry.getPages())
					{
						System.out.println(page.getPageTitle() + ", " + page.getURL());
					}
				}
				break;
				
			case SearchOptions.RESUME_INDEXING:
				System.out.println("RESUME_INDEXING received @" + Calendar.getInstance().getTime());
				try
				{
					StandaloneClusterClient.CONTAINER().syncNotifyRoot(new ResumeIndexingNotification());
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case SearchOptions.QUIT_SEARCH:
				break;
		}
	}

}
