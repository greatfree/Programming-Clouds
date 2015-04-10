package com.greatfree.testing.searcher;

import java.io.IOException;
import java.util.Set;

import com.greatfree.exceptions.RemoteReadException;
import com.greatfree.remote.RemoteReader;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.IsPublisherExistedRequest;
import com.greatfree.testing.message.IsPublisherExistedResponse;
import com.greatfree.testing.message.SearchKeywordRequest;
import com.greatfree.testing.message.SearchKeywordResponse;
import com.greatfree.util.NodeID;

/*
 * The class wraps the class, RemoteReader, to send search requests to the remote server and wait until search results are received. 11/29/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class SearchReader
{
	public static boolean isPublisherExisted(String url)
	{
		try
		{
			return ((IsPublisherExistedResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_SEARCH, new IsPublisherExistedRequest(url)))).isExisted();
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
		return false;
	}

	public static Set<String> searchKeyword(String keyword)
	{
		try
		{
			return ((SearchKeywordResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_SEARCH, new SearchKeywordRequest(keyword)))).getLinks();
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
		return SearchConfig.NO_LINKS;
	}
}
