package org.greatfree.app.search.container.cluster.storage;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.app.search.container.cluster.message.CrawledPagesNotification;
import org.greatfree.app.search.container.cluster.message.LocationNotification;
import org.greatfree.app.search.container.cluster.message.SearchApplicationID;
import org.greatfree.app.search.container.cluster.message.SearchMultiRequest;
import org.greatfree.app.search.multicast.child.storage.PageStorage;
import org.greatfree.app.search.multicast.child.storage.UserPreferences;
import org.greatfree.app.search.multicast.message.SearchMultiResponse;
import org.greatfree.cluster.ChildTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.ServerStatus;

// Created: 01/14/2019, Bing Li
class StorageTask implements ChildTask
{

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case SearchApplicationID.CRAWLED_PAGES_NOTIFICATION:
				System.out.println("CRAWLED_PAGES_NOTIFICATION received @" + Calendar.getInstance().getTime());
				CrawledPagesNotification cpn = (CrawledPagesNotification)notification;
				PageStorage.STORAGE().save(cpn.getPage());
				break;
				
			case SearchApplicationID.LOCATION_NOTIFICATION:
				System.out.println("LOCATION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				LocationNotification ln = (LocationNotification)notification;
				UserPreferences.STORAGE().setLocale(ln.getUserKey(), ln.isInternational());
				break;
				
			case SearchApplicationID.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ServerStatus.FREE().setShutdown();
				try
				{
					StorageNode.CLUSTER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public MulticastResponse processRequest(ClusterRequest request)
	{
		switch (request.getApplicationID())
		{
			case SearchApplicationID.SEARCH_MULTI_REQUEST:
				System.out.println("SEARCH_MULTI_REQUEST received @" + Calendar.getInstance().getTime());
				SearchMultiRequest smr = (SearchMultiRequest)request;
				return new SearchMultiResponse(PageStorage.STORAGE().search(smr.getUserKey(), smr.getQuery()), smr.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	@Override
	public void processDestinationNotification(InterChildrenNotification notification)
	{
		// TODO Auto-generated method stub
		
	}
	*/

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(CollectedClusterResponse response)
	{
		// TODO Auto-generated method stub
		
	}

}
