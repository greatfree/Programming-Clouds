package org.greatfree.testing.searcher;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.message.IsPublisherExistedRequest;
import org.greatfree.testing.message.IsPublisherExistedResponse;
import org.greatfree.testing.message.SearchKeywordRequest;
import org.greatfree.testing.message.SearchKeywordResponse;

/*
 * The process intends to illustrate the sample code for a user end. Through it, a user sends search requests to the coordinator. And then, the coordinator searches within its clusters via anycast or broadcast and responds to the user. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class StartSearcher
{
	/*
	 * The unique entry and exit of the searcher process. 11/29/2014, Bing Li
	 */
	public static void main(String[] args)
	{
		// Start the search client. 11/29/2014, Bing Li
		Searcher.CLIENT().start(ServerConfig.SEARCH_CLIENT_PORT);
		
		try
		{
			// Send a search request to the coordinator and wait for the response. This request is processed as an anycast request on the coordinator server. 11/29/2014, Bing Li
			IsPublisherExistedResponse response = (IsPublisherExistedResponse)RemoteReader.REMOTE().read(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_SEARCH, new IsPublisherExistedRequest("http://greatfree.blog.com"));
			// Display the result. 11/29/2014, Bing Li
			System.out.println(response.isExisted());
		}
		catch (RemoteReadException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			// Send a search request to the coordinator and wait for the response. This request is processed as a broadcast request on the coordinator server. 11/29/2014, Bing Li
			SearchKeywordResponse response = (SearchKeywordResponse)RemoteReader.REMOTE().read(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_SEARCH, new SearchKeywordRequest("greatfree labs"));
			// Display the result. 11/29/2014, Bing Li
			for (String link : response.getLinks())
			{
				System.out.println(link);
			}
		}
		catch (RemoteReadException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			// Shutdown the search client. 11/29/2014, Bing Li
			Searcher.CLIENT().stop();
		}
		catch (InterruptedException | ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
	}
}
