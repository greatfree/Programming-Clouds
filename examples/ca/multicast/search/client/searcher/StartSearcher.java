package ca.multicast.search.client.searcher;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

import ca.multicast.search.message.ClientSearchQueryRequest;
import ca.multicast.search.message.ClientSearchQueryResponse;
import ca.multicast.search.message.SearchQueryResponse;

// Created: 03/16/2020, Bing Li
class StartSearcher
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Your query: ");
		String query = in.nextLine();
		ClientSearchQueryResponse response;
		int index = 0;
		Searcher.FRONT().init();
		while (!query.equals("q"))
		{
			response = (ClientSearchQueryResponse)Searcher.FRONT().read(new ClientSearchQueryRequest(query));
			for (SearchQueryResponse entry : response.getResults())
			{
				System.out.println(++index + ") " + entry.getResult());
			}
			System.out.println("Type another query: ");
			query = in.nextLine();
			index = 0;
		}
		Searcher.FRONT().dispose();
		in.close();
	}

}
